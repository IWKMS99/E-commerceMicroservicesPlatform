package iwkms.shop.ecommerce.order.service;

import iwkms.shop.ecommerce.cart.dto.CartDto;
import iwkms.shop.ecommerce.cart.dto.CartItemDto;
import iwkms.shop.ecommerce.cart.service.CartService;
import iwkms.shop.ecommerce.order.dto.OrderCreateDto;
import iwkms.shop.ecommerce.order.dto.OrderDto;
import iwkms.shop.ecommerce.order.entity.Order;
import iwkms.shop.ecommerce.order.mapper.OrderMapper;
import iwkms.shop.ecommerce.order.repository.OrderRepository;
import iwkms.shop.ecommerce.shared.event.DomainEventPublisher;
import iwkms.shop.ecommerce.shared.event.OrderCreatedEvent;
import iwkms.shop.ecommerce.shared.exception.EmptyCartException;
import iwkms.shop.ecommerce.user.entity.Role;
import iwkms.shop.ecommerce.user.entity.User;
import iwkms.shop.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartService cartService;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private CartDto cartDto;
    private OrderCreateDto orderCreateDto;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        CartItemDto cartItem = new CartItemDto(UUID.randomUUID(), "Test Product", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));
        cartDto = new CartDto(user.getEmail(), Collections.singletonList(cartItem), new BigDecimal("20.00"));

        orderCreateDto = new OrderCreateDto("123 Main St, Anytown");
    }

    @Test
    void createOrder_whenCartIsNotEmpty_shouldCreateOrderAndPublishEvent() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartService.getCart(anyString())).thenReturn(cartDto);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toDto(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return new OrderDto(order.getId(), order.getUserId(), order.getStatus(), order.getTotalPrice(), order.getShippingAddress(), order.getCreatedAt(), Collections.emptyList());
        });

        OrderDto result = orderService.createOrder(user.getEmail(), orderCreateDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.userId());
        assertEquals(new BigDecimal("20.00"), result.totalPrice());

        verify(cartService).clearCart(user.getEmail());

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publishOrderCreated(eventCaptor.capture());

        OrderCreatedEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        assertEquals(user.getId(), publishedEvent.getUserId());
        assertEquals(1, publishedEvent.getItems().size());
        assertEquals(cartDto.items().getFirst().productId(), publishedEvent.getItems().getFirst().productId());
        assertEquals(2, publishedEvent.getItems().getFirst().quantity());
    }

    @Test
    void createOrder_whenCartIsEmpty_shouldThrowException() {
        CartDto emptyCart = new CartDto(user.getEmail(), Collections.emptyList(), BigDecimal.ZERO);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartService.getCart(anyString())).thenReturn(emptyCart);

        assertThrows(
                EmptyCartException.class,
                () -> orderService.createOrder(user.getEmail(), orderCreateDto)
        );
    }
}