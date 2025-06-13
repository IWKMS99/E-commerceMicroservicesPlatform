package iwkms.shop.ecommerce.order.controller;

import iwkms.shop.ecommerce.order.entity.Order;
import iwkms.shop.ecommerce.order.entity.OrderStatus;
import iwkms.shop.ecommerce.order.repository.OrderRepository;
import iwkms.shop.ecommerce.user.entity.Role;
import iwkms.shop.ecommerce.user.entity.User;
import iwkms.shop.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class OrderOwnershipIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.data.redis.repositories.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User ownerUser = new User();
        ownerUser.setEmail("owner@example.com");
        ownerUser.setPassword("pass");
        ownerUser.setFirstName("Owner");
        ownerUser.setRole(Role.USER);
        userRepository.save(ownerUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("pass");
        anotherUser.setFirstName("Another");
        anotherUser.setRole(Role.USER);
        userRepository.save(anotherUser);

        testOrder = new Order();
        testOrder.setUserId(ownerUser.getId());
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(BigDecimal.ONE);
        orderRepository.save(testOrder);
    }

    @Test
    @WithMockUser("owner@example.com")
    void whenUserIsOwner_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}", testOrder.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("another@example.com")
    void whenUserIsNotOwner_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}", testOrder.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void whenUserIsAdmin_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}", testOrder.getId()))
                .andExpect(status().isNotFound());
    }
}