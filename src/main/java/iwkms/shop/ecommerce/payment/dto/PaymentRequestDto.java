package iwkms.shop.ecommerce.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PaymentRequestDto(
        @NotNull UUID orderId
        // TODO: String cardNumber; String cardHolder;
) {}