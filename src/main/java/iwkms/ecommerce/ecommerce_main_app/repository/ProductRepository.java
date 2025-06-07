package iwkms.ecommerce.ecommerce_main_app.repository;

import iwkms.ecommerce.ecommerce_main_app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}