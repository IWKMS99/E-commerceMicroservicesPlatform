package iwkms.shop.ecommerce.inventory.listener;

import iwkms.shop.ecommerce.catalog.entity.Category;
import iwkms.shop.ecommerce.catalog.entity.Product;
import iwkms.shop.ecommerce.catalog.repository.CategoryRepository;
import iwkms.shop.ecommerce.catalog.repository.ProductRepository;
import iwkms.shop.ecommerce.inventory.entity.Inventory;
import iwkms.shop.ecommerce.inventory.repository.InventoryRepository;
import iwkms.shop.ecommerce.shared.event.OrderCreatedEvent;
import iwkms.shop.ecommerce.shared.event.OrderItemMessage;
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
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class OrderEventListenerIntegrationTest {

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
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Test Category");
        categoryRepository.save(category);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.TEN);
        testProduct.setCategory(category);
        productRepository.save(testProduct);

        Inventory inventory = new Inventory();
        inventory.setProductId(testProduct.getId());
        inventory.setQuantity(100);
        inventoryRepository.save(inventory);
    }

    @Test
    void handleOrderCreatedEvent_shouldDecreaseStock() {
        OrderItemMessage orderItemMessage = new OrderItemMessage(testProduct.getId(), 5);

        OrderCreatedEvent event = new OrderCreatedEvent(
                this,
                UUID.randomUUID(),
                UUID.randomUUID(),
                Collections.singletonList(orderItemMessage)
        );

        eventPublisher.publishEvent(event);

        Optional<Inventory> updatedInventory = inventoryRepository.findById(testProduct.getId());

        assertTrue(updatedInventory.isPresent());
        assertEquals(95, updatedInventory.get().getQuantity());
    }
}