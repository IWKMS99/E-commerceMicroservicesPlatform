package iwkms.shop.ecommerce.payment.controller;

import iwkms.shop.ecommerce.payment.dto.PaymentDto;
import iwkms.shop.ecommerce.payment.dto.PaymentRequestDto;
import iwkms.shop.ecommerce.payment.entity.PaymentStatus;
import iwkms.shop.ecommerce.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void createPayment_ShouldReturnPaymentDto() {
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        PaymentRequestDto requestDto = new PaymentRequestDto(orderId);
        
        PaymentDto expectedDto = new PaymentDto(
            paymentId,
            orderId,
            new BigDecimal("100.00"),
            PaymentStatus.COMPLETED,
            LocalDateTime.now()
        );

        when(paymentService.processPayment(any(PaymentRequestDto.class)))
            .thenReturn(expectedDto);

        ResponseEntity<PaymentDto> response = paymentController.createPayment(requestDto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(expectedDto);
    }
} 