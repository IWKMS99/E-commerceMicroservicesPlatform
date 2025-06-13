package iwkms.shop.ecommerce.payment.dto;
import iwkms.shop.ecommerce.payment.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDto(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime createdAt
) {}