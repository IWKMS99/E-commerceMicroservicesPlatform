package iwkms.shop.ecommerce.catalog.repository;

import iwkms.shop.ecommerce.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCategoryId(Long categoryId);
}