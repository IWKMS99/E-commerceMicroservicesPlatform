package iwkms.shop.ecommerce.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String categoryName
) {}