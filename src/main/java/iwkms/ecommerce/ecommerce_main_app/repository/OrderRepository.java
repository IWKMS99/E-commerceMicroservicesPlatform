package iwkms.ecommerce.ecommerce_main_app.repository;

import iwkms.ecommerce.ecommerce_main_app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}