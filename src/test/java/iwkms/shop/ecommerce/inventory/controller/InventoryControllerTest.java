package iwkms.shop.ecommerce.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iwkms.shop.ecommerce.config.CustomAuthenticationEntryPoint;
import iwkms.shop.ecommerce.config.SecurityConfig;
import iwkms.shop.ecommerce.inventory.dto.StockUpdateDto;
import iwkms.shop.ecommerce.inventory.entity.Inventory;
import iwkms.shop.ecommerce.inventory.service.InventoryService;
import iwkms.shop.ecommerce.shared.exception.GlobalExceptionHandler;
import iwkms.shop.ecommerce.shared.exception.ResourceNotFoundException;
import iwkms.shop.ecommerce.user.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(controllers = InventoryController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private UUID productId;
    private StockUpdateDto stockUpdateDto;
    private Inventory inventory;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        productId = UUID.randomUUID();
        stockUpdateDto = new StockUpdateDto(productId, 100);
        inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(150);

        doAnswer(invocation -> {
            invocation.getArgument(1, HttpServletResponse.class).sendError(401, "Unauthorized");
            return null;
        }).when(customAuthenticationEntryPoint).commence(any(), any(), any());
    }

    @Test
    @DisplayName("Успешное обновление запасов для ADMIN")
    @WithMockUser(roles = "ADMIN")
    void updateStock_whenAdminAndValidData_shouldReturnOk() throws Exception {
        when(inventoryService.addStock(eq(productId), eq(100))).thenReturn(inventory);

        mockMvc.perform(post("/api/v1/inventory/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(150));

        verify(inventoryService).addStock(productId, 100);
    }

    @Test
    @DisplayName("Возвращает 403 Forbidden, если пользователь не ADMIN")
    @WithMockUser(roles = "USER")
    void updateStock_whenUserIsNotAdmin_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/inventory/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUpdateDto)))
                .andExpect(status().isForbidden());

        verify(inventoryService, never()).addStock(any(), anyInt());
    }

    @Test
    @DisplayName("Возвращает 401 Unauthorized, если пользователь не аутентифицирован")
    @WithAnonymousUser
    void updateStock_whenUserIsNotAuthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/inventory/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUpdateDto)))
                .andExpect(status().isUnauthorized());

        verify(inventoryService, never()).addStock(any(), anyInt());
    }


    @Test
    @DisplayName("Возвращает 400 Bad Request при невалидных данных (null productId)")
    @WithMockUser(roles = "ADMIN")
    void updateStock_whenProductIdIsNull_shouldReturnBadRequest() throws Exception {
        StockUpdateDto invalidDto = new StockUpdateDto(null, 100);

        mockMvc.perform(post("/api/v1/inventory/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("productId: must not be null"));

        verify(inventoryService, never()).addStock(any(), anyInt());
    }

    @Test
    @DisplayName("Обрабатывает ResourceNotFoundException из сервиса и возвращает 404 Not Found")
    @WithMockUser(roles = "ADMIN")
    void updateStock_whenServiceThrowsResourceNotFound_shouldReturnNotFound() throws Exception {
        String errorMessage = "Inventory record not found for product: " + productId;
        when(inventoryService.addStock(eq(productId), eq(100)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        mockMvc.perform(post("/api/v1/inventory/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(inventoryService).addStock(productId, 100);
    }
}