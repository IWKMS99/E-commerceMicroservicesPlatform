package iwkms.shop.ecommerce.shared.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class PaymentCompletedEvent extends ApplicationEvent {
    private final UUID paymentId;
    private final UUID orderId;
    private final BigDecimal amount;

    public PaymentCompletedEvent(Object source, UUID paymentId, UUID orderId, BigDecimal amount) {
        super(source);
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }
}