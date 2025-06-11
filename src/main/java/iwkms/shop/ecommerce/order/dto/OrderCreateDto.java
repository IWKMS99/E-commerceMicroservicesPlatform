package iwkms.shop.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderCreateDto(
        @NotBlank String shippingAddress
) {}