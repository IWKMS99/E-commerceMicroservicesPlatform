package iwkms.shop.ecommerce.catalog.repository;

import iwkms.shop.ecommerce.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}