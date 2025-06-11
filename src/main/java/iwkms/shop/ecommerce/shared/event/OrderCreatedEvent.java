package iwkms.shop.ecommerce.shared.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {

    private final UUID orderId;
    private final UUID userId;
    private final List<OrderItemMessage> items;

    public OrderCreatedEvent(Object source, UUID orderId, UUID userId, List<OrderItemMessage> items) {
        super(source);
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
    }
}