package iwkms.shop.ecommerce.order.controller;

import iwkms.shop.ecommerce.order.dto.OrderCreateDto;
import iwkms.shop.ecommerce.order.dto.OrderDto;
import iwkms.shop.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderCreateDto createDto, Authentication authentication) {
        String userEmail = authentication.getName();
        OrderDto createdOrder = orderService.createOrder(userEmail, createDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        List<OrderDto> orders = orderService.findOrdersByUser(userEmail);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable UUID orderId, Authentication authentication) {
        // TODO: Добавить проверку, что пользователь может смотреть только свой заказ
        OrderDto order = orderService.findOrderById(orderId);
        return ResponseEntity.ok(order);
    }
}