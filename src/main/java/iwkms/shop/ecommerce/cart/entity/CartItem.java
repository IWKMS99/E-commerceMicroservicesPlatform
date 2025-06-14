package iwkms.shop.ecommerce.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private UUID productId;
    private int quantity;
    private String productName; 
    private BigDecimal price;
}