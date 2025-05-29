package iwkms.ecommerce.ecommerce_main_app.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Cart {
    private Map<Long, CartItem> items;
    private BigDecimal grandTotal;

    public Cart() {
        this.items = new ConcurrentHashMap<>();
        this.grandTotal = BigDecimal.ZERO;
    }

    public void recalculateGrandTotal() {
        this.grandTotal = items.values().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(CartItem item) {
        CartItem existingItem = items.get(item.getProductId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            items.put(item.getProductId(), item);
        }
        recalculateGrandTotal();
    }

    public void updateItemQuantity(Long productId, int quantity) {
        CartItem item = items.get(productId);
        if (item != null) {
            if (quantity <= 0) {
                items.remove(productId);
            } else {
                item.setQuantity(quantity);
            }
            recalculateGrandTotal();
        }
    }

    public void removeItem(Long productId) {
        items.remove(productId);
        recalculateGrandTotal();
    }

    public void clear() {
        items.clear();
        recalculateGrandTotal();
    }
}