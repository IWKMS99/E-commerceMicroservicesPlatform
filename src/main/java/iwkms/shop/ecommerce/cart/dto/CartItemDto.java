package iwkms.shop.ecommerce.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal price,
        BigDecimal subtotal
) {}