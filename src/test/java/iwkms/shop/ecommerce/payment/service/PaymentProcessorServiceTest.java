package iwkms.shop.ecommerce.payment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorServiceTest {

    private final PaymentProcessorService paymentProcessorService = new PaymentProcessorService();

    @Test
    void processPayment_ShouldReturnTrue() {
        UUID orderId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        boolean result = paymentProcessorService.processPayment(orderId, amount);

        assertThat(result).isTrue();
    }
} 