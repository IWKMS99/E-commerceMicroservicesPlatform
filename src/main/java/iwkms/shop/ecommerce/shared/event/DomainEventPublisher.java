package iwkms.shop.ecommerce.shared.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishOrderCreated(OrderCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }
    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        eventPublisher.publishEvent(event);
    }
}