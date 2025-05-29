package iwkms.ecommerce.ecommerce_main_app.cart;

import iwkms.ecommerce.ecommerce_main_app.product.Product;
import iwkms.ecommerce.ecommerce_main_app.product.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final String CART_SESSION_KEY = "shoppingCart";
    private final ProductService productService;

    public Cart getCurrentCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addItemToCart(Long productId, Integer quantity, HttpSession session) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found.");
        }
        Product product = productOpt.get();

        Cart cart = getCurrentCart(session);
        CartItem cartItem = cart.getItems().get(productId);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem(productId, product.getName(), quantity, product.getPrice());
            cart.getItems().put(productId, cartItem);
        }
        cart.recalculateGrandTotal();
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void updateItemQuantity(Long productId, Integer quantity, HttpSession session) {
        Cart cart = getCurrentCart(session);
        CartItem cartItem = cart.getItems().get(productId);

        if (cartItem != null) {
            if (quantity <= 0) {
                cart.getItems().remove(productId);
            } else {
                cartItem.setQuantity(quantity);
            }
            cart.recalculateGrandTotal();
            session.setAttribute(CART_SESSION_KEY, cart);
        } else {
            throw new IllegalArgumentException("Product with ID " + productId + " not found in cart.");
        }
    }

    public void removeItemFromCart(Long productId, HttpSession session) {
        Cart cart = getCurrentCart(session);
        cart.getItems().remove(productId);
        cart.recalculateGrandTotal();
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        Cart cart = getCurrentCart(session);
        cart.clear();
        session.setAttribute(CART_SESSION_KEY, cart);
    }
}