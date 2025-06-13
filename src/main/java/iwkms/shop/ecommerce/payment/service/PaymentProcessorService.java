package iwkms.shop.ecommerce.payment.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentProcessorService {

    /**
     * Simulates a call to an external payment gateway.
     * In a real application, this would be an HTTP client to Stripe, PayPal, etc. API.
     * @return true if payment was successful, false otherwise.
     */
    public boolean processPayment(UUID orderId, BigDecimal amount) {
        System.out.printf("Processing payment for order %s in amount of %s... Success!%n", orderId, amount);
        return true;
    }
}