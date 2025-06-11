package iwkms.shop.ecommerce.order.service;

import iwkms.shop.ecommerce.cart.dto.CartDto;
import iwkms.shop.ecommerce.cart.service.CartService;
import iwkms.shop.ecommerce.order.dto.OrderCreateDto;
import iwkms.shop.ecommerce.order.dto.OrderDto;
import iwkms.shop.ecommerce.order.entity.Order;
import iwkms.shop.ecommerce.order.entity.OrderItem;
import iwkms.shop.ecommerce.order.entity.OrderStatus;
import iwkms.shop.ecommerce.order.mapper.OrderMapper;
import iwkms.shop.ecommerce.order.repository.OrderRepository;
import iwkms.shop.ecommerce.shared.event.OrderCreatedEvent;
import iwkms.shop.ecommerce.shared.event.DomainEventPublisher;
import iwkms.shop.ecommerce.shared.event.OrderItemMessage;
import iwkms.shop.ecommerce.shared.exception.ResourceNotFoundException;
import iwkms.shop.ecommerce.user.entity.User;
import iwkms.shop.ecommerce.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public OrderDto createOrder(String userEmail, OrderCreateDto createDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        CartDto cart = cartService.getCart(userEmail);
        if (cart.items().isEmpty()) {
            throw new IllegalStateException("Cannot create an order from an empty cart.");
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(createDto.shippingAddress());
        order.setTotalPrice(cart.total());

        List<OrderItemMessage> itemMessages = cart.items().stream()
                .map(cartItem -> new OrderItemMessage(cartItem.productId(), cartItem.quantity()))
                .toList();

        cart.items().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.productId());
            orderItem.setQuantity(cartItem.quantity());
            orderItem.setPrice(cartItem.price());
            order.addOrderItem(orderItem);
        });
        
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userEmail);

        eventPublisher.publishOrderCreated(
            new OrderCreatedEvent(this, savedOrder.getId(), savedOrder.getUserId(), itemMessages)
        );

        return orderMapper.toDto(savedOrder);
    }

    public List<OrderDto> findOrdersByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        
        return orderRepository.findByUserId(user.getId()).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrderDto findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
}