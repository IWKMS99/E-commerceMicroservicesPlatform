package iwkms.shop.ecommerce.cart.repository;

import iwkms.shop.ecommerce.cart.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class CartRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long CART_TTL = 30; 

    private String getKey(String userId) {
        return "cart:" + userId;
    }

    public Optional<Cart> findById(String userId) {
        Cart cart = (Cart) redisTemplate.opsForValue().get(getKey(userId));
        return Optional.ofNullable(cart);
    }

    public void save(Cart cart) {
        redisTemplate.opsForValue().set(getKey(cart.getId()), cart, CART_TTL, TimeUnit.DAYS);
    }

    public void deleteById(String userId) {
        redisTemplate.delete(getKey(userId));
    }
}