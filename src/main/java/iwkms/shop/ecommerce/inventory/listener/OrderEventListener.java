package iwkms.shop.ecommerce.inventory.listener;

import iwkms.shop.ecommerce.inventory.service.InventoryService;
import iwkms.shop.ecommerce.shared.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final InventoryService inventoryService;

    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for orderId: {}", event.getOrderId());
        try {
            inventoryService.reserveStock(event.getItems());
            log.info("Stock successfully reserved for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to reserve stock for orderId: {}. Reason: {}", event.getOrderId(), e.getMessage());
            // TODO: Здесь нужна логика компенсации.
            // Например, отменить заказ или пометить его как "ошибка резервации".
        }
    }
}