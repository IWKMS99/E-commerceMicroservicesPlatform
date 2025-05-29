package iwkms.ecommerce.ecommerce_main_app.cart;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

class CartItemRequest {
    public Long productId;
    public Integer quantity;
}

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(HttpSession session) {
        Cart cart = cartService.getCurrentCart(session);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(@RequestBody CartItemRequest itemRequest, HttpSession session) {
        if (itemRequest.productId == null || itemRequest.quantity == null || itemRequest.quantity <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            cartService.addItemToCart(itemRequest.productId, itemRequest.quantity, session);
            return ResponseEntity.ok(cartService.getCurrentCart(session));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Cart> updateItemQuantity(@PathVariable Long productId,
                                                   @RequestBody CartItemQuantityUpdateRequest quantityRequest,
                                                   HttpSession session) {
        if (quantityRequest.quantity == null ) {
            return ResponseEntity.badRequest().build();
        }
        try {
            cartService.updateItemQuantity(productId, quantityRequest.quantity, session);
            return ResponseEntity.ok(cartService.getCurrentCart(session));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
    static class CartItemQuantityUpdateRequest {
        public Integer quantity;
    }


    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long productId, HttpSession session) {
        cartService.removeItemFromCart(productId, session);
        return ResponseEntity.ok(cartService.getCurrentCart(session));
    }

    @DeleteMapping
    public ResponseEntity<Cart> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok(cartService.getCurrentCart(session));
    }
}