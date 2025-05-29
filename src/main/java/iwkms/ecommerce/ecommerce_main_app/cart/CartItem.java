package iwkms.ecommerce.ecommerce_main_app.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalPrice;

    public CartItem(Long productId, String productName, Integer quantity, BigDecimal pricePerUnit) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.calculateTotalPrice();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        if (this.pricePerUnit != null && this.quantity != null) {
            this.totalPrice = this.pricePerUnit.multiply(BigDecimal.valueOf(this.quantity));
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
}