package iwkms.shop.ecommerce.inventory.repository;

import iwkms.shop.ecommerce.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
}