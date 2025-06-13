package iwkms.shop.ecommerce.order.listener;

import iwkms.shop.ecommerce.order.entity.Order;
import iwkms.shop.ecommerce.order.entity.OrderStatus;
import iwkms.shop.ecommerce.order.repository.OrderRepository;
import iwkms.shop.ecommerce.shared.event.PaymentCompletedEvent;
import iwkms.shop.ecommerce.user.entity.Role;
import iwkms.shop.ecommerce.user.entity.User;
import iwkms.shop.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class PaymentEventListenerIntegrationTest {

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
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("test@user.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.USER);
        userRepository.save(user);

        testOrder = new Order();
        testOrder.setUserId(user.getId());
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(new BigDecimal("123.45"));
        orderRepository.save(testOrder);
    }

    @Test
    void handlePaymentCompletedEvent_shouldUpdateOrderStatusToPaid() {

        assertEquals(OrderStatus.PENDING, orderRepository.findById(testOrder.getId()).get().getStatus());

        PaymentCompletedEvent event = new PaymentCompletedEvent(
                this,
                UUID.randomUUID(),
                testOrder.getId(),
                testOrder.getTotalPrice()
        );

        eventPublisher.publishEvent(event);

        Optional<Order> updatedOrderOpt = orderRepository.findById(testOrder.getId());
        assertTrue(updatedOrderOpt.isPresent());
        assertEquals(OrderStatus.PAID, updatedOrderOpt.get().getStatus());
    }
}