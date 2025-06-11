package iwkms.shop.ecommerce.cart.service;

import iwkms.shop.ecommerce.cart.dto.AddItemToCartDto;
import iwkms.shop.ecommerce.cart.dto.CartDto;
import iwkms.shop.ecommerce.cart.dto.CartItemDto;
import iwkms.shop.ecommerce.cart.entity.Cart;
import iwkms.shop.ecommerce.cart.entity.CartItem;
import iwkms.shop.ecommerce.cart.repository.CartRepository;
import iwkms.shop.ecommerce.catalog.dto.ProductDto;
import iwkms.shop.ecommerce.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartDto getCart(String userId) {
        Cart cart = cartRepository.findById(userId).orElse(createEmptyCart(userId));
        return enrichAndMapToDto(cart);
    }
    
    @Transactional
    public CartDto addItemToCart(String userId, AddItemToCartDto itemDto) {
        productService.findProductById(itemDto.productId()); 
        
        Cart cart = cartRepository.findById(userId).orElse(createEmptyCart(userId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(itemDto.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + itemDto.quantity());
        } else {
            cart.getItems().add(new CartItem(itemDto.productId(), itemDto.quantity(), null, null));
        }

        cartRepository.save(cart);
        return enrichAndMapToDto(cart);
    }
    
    @Transactional
    public CartDto removeItemFromCart(String userId, UUID productId) {
        Cart cart = cartRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        cartRepository.save(cart);
        return enrichAndMapToDto(cart);
    }

    public void clearCart(String userId) {
        cartRepository.deleteById(userId);
    }

    private Cart createEmptyCart(String userId) {
        Cart cart = new Cart();
        cart.setId(userId);
        return cart;
    }
    
    private CartDto enrichAndMapToDto(Cart cart) {
        if (cart.getItems().isEmpty()) {
            return new CartDto(cart.getId(), Collections.emptyList(), BigDecimal.ZERO);
        }

        List<CartItemDto> enrichedItems = cart.getItems().stream().map(item -> {
            ProductDto product = productService.findProductById(item.getProductId());
            BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new CartItemDto(item.getProductId(), product.name(), item.getQuantity(), product.price(), subtotal);
        }).collect(Collectors.toList());

        BigDecimal total = enrichedItems.stream()
                .map(CartItemDto::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDto(cart.getId(), enrichedItems, total);
    }
}