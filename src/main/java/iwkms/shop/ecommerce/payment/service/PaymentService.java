package iwkms.shop.ecommerce.payment.service;

import iwkms.shop.ecommerce.order.dto.OrderDto;
import iwkms.shop.ecommerce.order.service.OrderService;
import iwkms.shop.ecommerce.payment.dto.PaymentDto;
import iwkms.shop.ecommerce.payment.dto.PaymentRequestDto;
import iwkms.shop.ecommerce.payment.entity.Payment;
import iwkms.shop.ecommerce.payment.entity.PaymentStatus;
import iwkms.shop.ecommerce.payment.mapper.PaymentMapper;
import iwkms.shop.ecommerce.payment.repository.PaymentRepository;
import iwkms.shop.ecommerce.shared.event.DomainEventPublisher;
import iwkms.shop.ecommerce.shared.event.PaymentCompletedEvent;
import iwkms.shop.ecommerce.shared.exception.PaymentFailedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PaymentProcessorService paymentProcessor;
    private final DomainEventPublisher eventPublisher;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentDto processPayment(PaymentRequestDto requestDto) {
        OrderDto order = orderService.findOrderById(requestDto.orderId());

        Payment initialPayment = new Payment();
        initialPayment.setOrderId(order.id());
        initialPayment.setAmount(order.totalPrice());
        initialPayment.setStatus(PaymentStatus.PROCESSING);

        Payment processingPayment = paymentRepository.save(initialPayment);

        boolean success = paymentProcessor.processPayment(order.id(), order.totalPrice());

        if (success) {
            processingPayment.setStatus(PaymentStatus.COMPLETED);
            processingPayment.setCompletedAt(LocalDateTime.now());

            Payment completedPayment = paymentRepository.save(processingPayment);

            eventPublisher.publishPaymentCompleted(
                    new PaymentCompletedEvent(this, completedPayment.getId(), order.id(), order.totalPrice())
            );

            return paymentMapper.toDto(completedPayment);

        } else {
            processingPayment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(processingPayment);

            throw new PaymentFailedException("Payment provider declined the transaction for order: " + order.id());
        }
    }
}