package iwkms.shop.ecommerce.shared.event;

import java.io.Serializable;
import java.util.UUID;

public record OrderItemMessage(
        UUID productId,
        int quantity
) implements Serializable {}