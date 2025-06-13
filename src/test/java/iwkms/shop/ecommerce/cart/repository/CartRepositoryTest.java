package iwkms.shop.ecommerce.cart.repository;

import iwkms.shop.ecommerce.cart.entity.Cart;
import iwkms.shop.ecommerce.cart.entity.CartItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class CartRepositoryTest {

    @Container
    private static final GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeEach
    void setUp() {
        if (redisContainer.isRunning()) {
            Assertions.assertNotNull(redisTemplate.getConnectionFactory());
            redisTemplate.getConnectionFactory().getConnection().serverCommands();
        }
    }

    @Test
    void findById_ShouldReturnCart_WhenCartExists() {
        String userId = "user-123";
        Cart cart = new Cart();
        cart.setId(userId);
        CartItem item = new CartItem(UUID.randomUUID(), 2, "Test Product", new BigDecimal("10.00"));
        cart.getItems().add(item);

        cartRepository.save(cart);

        Optional<Cart> foundCartOpt = cartRepository.findById(userId);

        assertThat(foundCartOpt).isPresent();
        Cart foundCart = foundCartOpt.get();
        assertThat(foundCart.getId()).isEqualTo(userId);
        assertThat(foundCart.getItems()).hasSize(1);
        assertThat(foundCart.getItems().getFirst().getProductName()).isEqualTo("Test Product");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenCartDoesNotExist() {
        String userId = "non-existent-user";

        Optional<Cart> foundCart = cartRepository.findById(userId);

        assertThat(foundCart).isEmpty();
    }

    @Test
    void save_ShouldStoreCartInRedis() {
        String userId = "user-to-save";
        Cart cart = new Cart();
        cart.setId(userId);
        cart.getItems().add(new CartItem(UUID.randomUUID(), 1, "Another Product", new BigDecimal("5.50")));

        cartRepository.save(cart);

        Cart savedCart = (Cart) redisTemplate.opsForValue().get("cart:" + userId);
        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getId()).isEqualTo(userId);
        assertThat(savedCart.getItems()).hasSize(1);
    }

    @Test
    void save_ShouldSetTtlOnCart() {
        String userId = "user-with-ttl";
        Cart cart = new Cart();
        cart.setId(userId);

        cartRepository.save(cart);

        Long ttl = redisTemplate.getExpire("cart:" + userId, TimeUnit.SECONDS);
        assertThat(ttl).isNotNull();
        assertThat(ttl).isGreaterThan(TimeUnit.DAYS.toSeconds(29));
    }


    @Test
    void deleteById_ShouldRemoveCartFromRedis() {
        String userId = "user-to-delete";
        Cart cart = new Cart();
        cart.setId(userId);
        cartRepository.save(cart);

        assertThat(cartRepository.findById(userId)).isPresent();

        cartRepository.deleteById(userId);

        assertThat(cartRepository.findById(userId)).isEmpty();
        Object deletedCart = redisTemplate.opsForValue().get("cart:" + userId);
        assertThat(deletedCart).isNull();
    }
}