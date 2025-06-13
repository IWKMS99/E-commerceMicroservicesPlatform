package iwkms.shop.ecommerce.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iwkms.shop.ecommerce.cart.dto.AddItemToCartDto;
import iwkms.shop.ecommerce.cart.dto.CartDto;
import iwkms.shop.ecommerce.cart.dto.CartItemDto;
import iwkms.shop.ecommerce.cart.service.CartService;
import iwkms.shop.ecommerce.user.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings({"unused"})
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private final String TEST_USER_ID = "test-user";

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void getCart_ShouldReturnCartDto() throws Exception {
        UUID productId = UUID.randomUUID();
        CartItemDto itemDto = new CartItemDto(productId, "Test Product", 1, new BigDecimal("10.00"), new BigDecimal("10.00"));
        CartDto cartDto = new CartDto(TEST_USER_ID, List.of(itemDto), new BigDecimal("10.00"));
        when(cartService.getCart(TEST_USER_ID)).thenReturn(cartDto);

        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.items[0].productName").value("Test Product"))
                .andExpect(jsonPath("$.total").value(10.00));
    }

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void addItem_ShouldReturnUpdatedCart() throws Exception {
        UUID productId = UUID.randomUUID();
        AddItemToCartDto addItemDto = new AddItemToCartDto(productId, 2);
        CartDto updatedCartDto = new CartDto(TEST_USER_ID, Collections.emptyList(), new BigDecimal("20.00"));
        when(cartService.addItemToCart(eq(TEST_USER_ID), any(AddItemToCartDto.class))).thenReturn(updatedCartDto);

        mockMvc.perform(post("/api/v1/cart/items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.total").value(20.00));
    }

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void addItem_ShouldFailValidation_WhenQuantityIsZero() throws Exception {
        //noinspection ConstantConditions
        AddItemToCartDto addItemDto = new AddItemToCartDto(UUID.randomUUID(), 0);

        mockMvc.perform(post("/api/v1/cart/items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void removeItem_ShouldReturnUpdatedCart() throws Exception {
        UUID productId = UUID.randomUUID();
        CartDto updatedCartDto = new CartDto(TEST_USER_ID, Collections.emptyList(), BigDecimal.ZERO);
        when(cartService.removeItemFromCart(TEST_USER_ID, productId)).thenReturn(updatedCartDto);

        mockMvc.perform(delete("/api/v1/cart/items/{productId}", productId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void clearCart_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/cart")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}