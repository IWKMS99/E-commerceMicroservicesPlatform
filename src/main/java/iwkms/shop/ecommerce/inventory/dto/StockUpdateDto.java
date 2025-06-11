package iwkms.shop.ecommerce.inventory.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StockUpdateDto(
        @NotNull UUID productId,
        int quantity
) {}