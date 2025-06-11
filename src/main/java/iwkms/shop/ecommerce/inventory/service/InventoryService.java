package iwkms.shop.ecommerce.inventory.service;

import iwkms.shop.ecommerce.inventory.entity.Inventory;
import iwkms.shop.ecommerce.inventory.repository.InventoryRepository;
import iwkms.shop.ecommerce.shared.event.OrderItemMessage;
import iwkms.shop.ecommerce.shared.exception.InsufficientStockException;
import iwkms.shop.ecommerce.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void reserveStock(List<OrderItemMessage> items) {
        for (OrderItemMessage item : items) {
            log.info("Reserving stock for product {}: {} items", item.productId(), item.quantity());
            Inventory inventory = inventoryRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found for product: " + item.productId()));

            if (inventory.getQuantity() < item.quantity()) {
                throw new InsufficientStockException("Not enough stock for product: " + item.productId());
            }

            inventory.setQuantity(inventory.getQuantity() - item.quantity());
            inventoryRepository.save(inventory);
        }
    }

    @Transactional
    public Inventory addStock(UUID productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setProductId(productId);
                    newInventory.setQuantity(0);
                    return newInventory;
                });

        inventory.setQuantity(inventory.getQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }
}