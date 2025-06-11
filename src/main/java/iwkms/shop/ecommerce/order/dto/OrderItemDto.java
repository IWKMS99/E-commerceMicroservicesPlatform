package iwkms.shop.ecommerce.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
        Long id,
        UUID productId,
        int quantity,
        BigDecimal price
) {}