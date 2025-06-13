package iwkms.shop.ecommerce.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iwkms.shop.ecommerce.config.JwtAuthenticationFilter;
import iwkms.shop.ecommerce.order.dto.OrderCreateDto;
import iwkms.shop.ecommerce.order.dto.OrderDto;
import iwkms.shop.ecommerce.order.service.OrderService;
import iwkms.shop.ecommerce.shared.exception.GlobalExceptionHandler;
import iwkms.shop.ecommerce.shared.exception.ResourceNotFoundException;
import iwkms.shop.ecommerce.user.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "test@user.com")
    void createOrder_whenValidRequest_shouldReturnCreated() throws Exception {
        OrderCreateDto createDto = new OrderCreateDto("123 Test St");
        when(orderService.createOrder(anyString(), any(OrderCreateDto.class)))
                .thenReturn(new OrderDto(UUID.randomUUID(), null, null, null, null, null, null));

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test@user.com")
    void createOrder_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        OrderCreateDto createDto = new OrderCreateDto(" ");

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@user.com")
    void getUserOrders_shouldReturnOrderList() throws Exception {
        when(orderService.findOrdersByUser(anyString()))
                .thenReturn(Collections.singletonList(new OrderDto(UUID.randomUUID(), null, null, null, null, null, null)));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "test@user.com")
    void getOrderById_whenOrderExistsAndBelongsToUser_shouldReturnOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.findOrderByIdForUser(orderId, "test@user.com"))
                .thenReturn(new OrderDto(orderId, null, null, null, null, null, null));

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    @WithMockUser(username = "test@user.com")
    void getOrderById_whenOrderDoesNotExist_shouldReturnNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.findOrderByIdForUser(orderId, "test@user.com"))
                .thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
    }
}