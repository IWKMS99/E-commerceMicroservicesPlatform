package iwkms.shop.ecommerce.cart.service;

import iwkms.shop.ecommerce.cart.dto.AddItemToCartDto;
import iwkms.shop.ecommerce.cart.dto.CartDto;
import iwkms.shop.ecommerce.cart.entity.Cart;
import iwkms.shop.ecommerce.cart.entity.CartItem;
import iwkms.shop.ecommerce.cart.repository.CartRepository;
import iwkms.shop.ecommerce.catalog.dto.ProductDto;
import iwkms.shop.ecommerce.catalog.service.ProductService;
import iwkms.shop.ecommerce.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private String userId;
    private UUID productId;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        userId = "user-123";
        productId = UUID.randomUUID();
        productDto = new ProductDto(productId, "Test Product", "Description", new BigDecimal("99.99"), 1L, "Category");
    }

    @Test
    void getCart_ShouldReturnExistingCart() {
        Cart cart = new Cart();
        cart.setId(userId);
        cart.getItems().add(new CartItem(productId, 1, "Test Product", new BigDecimal("99.99")));

        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));
        when(productService.findProductById(productId)).thenReturn(productDto);

        CartDto result = cartService.getCart(userId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().productId()).isEqualTo(productId);
        assertThat(result.total()).isEqualTo(new BigDecimal("99.99"));
    }

    @Test
    void getCart_ShouldCreateEmptyCart_WhenNotFound() {
        when(cartRepository.findById(userId)).thenReturn(Optional.empty());

        CartDto result = cartService.getCart(userId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.items()).isEmpty();
        assertThat(result.total()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void addItemToCart_ShouldAddNewItemToEmptyCart() {
        AddItemToCartDto itemDto = new AddItemToCartDto(productId, 2);
        when(productService.findProductById(productId)).thenReturn(productDto);
        when(cartRepository.findById(userId)).thenReturn(Optional.empty());

        CartDto result = cartService.addItemToCart(userId, itemDto);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().quantity()).isEqualTo(2);
        assertThat(result.items().getFirst().productId()).isEqualTo(productId);
        assertThat(result.total()).isEqualTo(new BigDecimal("199.98"));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItemToCart_ShouldIncreaseQuantityOfExistingItem() {
        AddItemToCartDto itemDto = new AddItemToCartDto(productId, 1);

        Cart existingCart = new Cart();
        existingCart.setId(userId);
        existingCart.setItems(new ArrayList<>());
        existingCart.getItems().add(new CartItem(productId, 2, "Test Product", new BigDecimal("99.99")));

        when(productService.findProductById(productId)).thenReturn(productDto);
        when(cartRepository.findById(userId)).thenReturn(Optional.of(existingCart));

        CartDto result = cartService.addItemToCart(userId, itemDto);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().quantity()).isEqualTo(3);
        assertThat(result.total()).isEqualTo(new BigDecimal("299.97"));
        verify(cartRepository, times(1)).save(existingCart);
    }

    @Test
    void addItemToCart_ShouldThrowException_WhenProductNotFound() {
        AddItemToCartDto itemDto = new AddItemToCartDto(productId, 1);
        when(productService.findProductById(productId)).thenThrow(new ResourceNotFoundException("Product not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartService.addItemToCart(userId, itemDto));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void removeItemFromCart_ShouldRemoveItemSuccessfully() {
        Cart existingCart = new Cart();
        existingCart.setId(userId);
        existingCart.setItems(new ArrayList<>());
        existingCart.getItems().add(new CartItem(productId, 1, "Test Product", new BigDecimal("99.99")));

        when(cartRepository.findById(userId)).thenReturn(Optional.of(existingCart));

        CartDto result = cartService.removeItemFromCart(userId, productId);

        assertThat(result.items()).isEmpty();
        assertThat(result.total()).isEqualTo(BigDecimal.ZERO);
        verify(cartRepository, times(1)).save(existingCart);
    }

    @Test
    void removeItemFromCart_ShouldThrowException_WhenCartNotFound() {
        when(cartRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.removeItemFromCart(userId, productId));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void clearCart_ShouldCallDeleteOnRepository() {

        cartService.clearCart(userId);

        verify(cartRepository, times(1)).deleteById(userId);
    }
}