package iwkms.shop.ecommerce.cart.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@RedisHash("Cart")
public class Cart implements Serializable {
    @Id
    private String id;

    private List<CartItem> items = new ArrayList<>();
}