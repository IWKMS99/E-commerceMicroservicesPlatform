package iwkms.shop.ecommerce.cart.controller;

import iwkms.shop.ecommerce.cart.dto.AddItemToCartDto;
import iwkms.shop.ecommerce.cart.dto.CartDto;
import iwkms.shop.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private String getUserId(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart(Authentication authentication) {
        String userId = getUserId(authentication);
        CartDto cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(@Valid @RequestBody AddItemToCartDto itemDto, Authentication authentication) {
        String userId = getUserId(authentication);
        CartDto updatedCart = cartService.addItemToCart(userId, itemDto);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartDto> removeItem(@PathVariable UUID productId, Authentication authentication) {
        String userId = getUserId(authentication);
        CartDto updatedCart = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String userId = getUserId(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}