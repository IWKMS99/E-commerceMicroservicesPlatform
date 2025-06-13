package iwkms.shop.ecommerce.order.listener;

import iwkms.shop.ecommerce.order.entity.Order;
import iwkms.shop.ecommerce.order.entity.OrderStatus;
import iwkms.shop.ecommerce.order.repository.OrderRepository;
import iwkms.shop.ecommerce.shared.event.PaymentCompletedEvent;
import iwkms.shop.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderRepository orderRepository;

    @EventListener
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent for orderId: {}", event.getOrderId());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found during payment completion: " + event.getOrderId()));

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        log.info("Order {} status updated to PAID", event.getOrderId());
    }
}