package iwkms.shop.ecommerce.order.mapper;

import iwkms.shop.ecommerce.order.dto.OrderDto;
import iwkms.shop.ecommerce.order.dto.OrderItemDto;
import iwkms.shop.ecommerce.order.entity.Order;
import iwkms.shop.ecommerce.order.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
    OrderItemDto toItemDto(OrderItem orderItem);
}