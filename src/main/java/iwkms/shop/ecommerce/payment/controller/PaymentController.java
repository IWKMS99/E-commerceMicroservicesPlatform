package iwkms.shop.ecommerce.payment.controller;

import iwkms.shop.ecommerce.payment.dto.PaymentDto;
import iwkms.shop.ecommerce.payment.dto.PaymentRequestDto;
import iwkms.shop.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody PaymentRequestDto requestDto) {
        PaymentDto paymentResult = paymentService.processPayment(requestDto);
        return ResponseEntity.ok(paymentResult);
    }
}