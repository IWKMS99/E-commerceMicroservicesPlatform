package iwkms.shop.ecommerce.inventory.controller;

import iwkms.shop.ecommerce.inventory.dto.InventoryDto;
import iwkms.shop.ecommerce.inventory.dto.StockUpdateDto;
import iwkms.shop.ecommerce.inventory.entity.Inventory;
import iwkms.shop.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryDto> updateStock(@Valid @RequestBody StockUpdateDto stockUpdateDto) {
        Inventory updatedInventory = inventoryService.addStock(
                stockUpdateDto.productId(),
                stockUpdateDto.quantity()
        );
        return ResponseEntity.ok(new InventoryDto(updatedInventory.getProductId(), updatedInventory.getQuantity()));
    }
}