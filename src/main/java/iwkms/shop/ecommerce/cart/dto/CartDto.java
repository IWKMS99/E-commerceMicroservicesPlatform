package iwkms.shop.ecommerce.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        String id,
        List<CartItemDto> items,
        BigDecimal total
) {}