package iwkms.shop.ecommerce.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddItemToCartDto(
        @NotNull UUID productId,
        @Min(1) int quantity
) {}