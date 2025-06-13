package iwkms.shop.ecommerce.payment.repository;

import iwkms.shop.ecommerce.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {}