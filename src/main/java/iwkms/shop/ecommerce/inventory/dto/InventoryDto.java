package iwkms.shop.ecommerce.inventory.dto;

import java.util.UUID;

public record InventoryDto(UUID productId, int quantity) {}