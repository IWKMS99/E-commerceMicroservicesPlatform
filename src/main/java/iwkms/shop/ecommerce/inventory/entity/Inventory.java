package iwkms.shop.ecommerce.inventory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "inventory", schema = "inventory_management")
public class Inventory {
    @Id
    private UUID productId;

    @Column(nullable = false)
    private int quantity;
}