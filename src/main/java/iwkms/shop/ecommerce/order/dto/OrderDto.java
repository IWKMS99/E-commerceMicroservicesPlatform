package iwkms.shop.ecommerce.order.dto;

import iwkms.shop.ecommerce.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        UUID userId,
        OrderStatus status,
        BigDecimal totalPrice,
        String shippingAddress,
        LocalDateTime createdAt,
        List<OrderItemDto> items
) {}