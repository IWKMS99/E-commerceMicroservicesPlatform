package iwkms.shop.ecommerce.payment.mapper;

import iwkms.shop.ecommerce.payment.dto.PaymentDto;
import iwkms.shop.ecommerce.payment.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);
}