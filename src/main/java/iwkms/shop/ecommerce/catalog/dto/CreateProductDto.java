package iwkms.shop.ecommerce.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateProductDto(
        @NotBlank String name,
        String description,
        @NotNull @Positive BigDecimal price,
        @NotNull Long categoryId
) {}