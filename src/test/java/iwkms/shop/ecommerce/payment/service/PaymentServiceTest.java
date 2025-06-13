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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentProcessorService paymentProcessor;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private OrderDto orderDto;
    private PaymentRequestDto paymentRequestDto;
    private Payment payment;
    private PaymentDto paymentDto;

    @BeforeEach
    void setUp() {
        UUID orderId = UUID.randomUUID();
        orderDto = new OrderDto(orderId, UUID.randomUUID(), null, new BigDecimal("100.00"), "", LocalDateTime.now(), null);
        paymentRequestDto = new PaymentRequestDto(orderId);

        payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setOrderId(orderId);
        payment.setAmount(orderDto.totalPrice());
        payment.setStatus(PaymentStatus.PROCESSING);
    }


    @Test
    void processPayment_whenPaymentIsSuccessful_shouldCompletePaymentAndPublishEvent() {
        when(orderService.findOrderById(any(UUID.class))).thenReturn(orderDto);
        when(paymentProcessor.processPayment(any(UUID.class), any(BigDecimal.class))).thenReturn(true);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment original = invocation.getArgument(0);
            Payment copy = new Payment();
            copy.setId(original.getId());
            copy.setOrderId(original.getOrderId());
            copy.setAmount(original.getAmount());
            copy.setStatus(original.getStatus());
            copy.setCreatedAt(original.getCreatedAt());
            copy.setCompletedAt(original.getCompletedAt());
            return copy;
        });

        when(paymentMapper.toDto(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            return new PaymentDto(p.getId(), p.getOrderId(), p.getAmount(), p.getStatus(), p.getCreatedAt());
        });

        PaymentDto result = paymentService.processPayment(paymentRequestDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, result.status());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(2)).save(paymentCaptor.capture());

        Payment initialPayment = paymentCaptor.getAllValues().get(0);
        Payment finalPayment = paymentCaptor.getAllValues().get(1);

        assertEquals(PaymentStatus.PROCESSING, initialPayment.getStatus());
        assertEquals(PaymentStatus.COMPLETED, finalPayment.getStatus());
        assertNotNull(finalPayment.getCompletedAt());

        verify(eventPublisher).publishPaymentCompleted(any(PaymentCompletedEvent.class));
    }

    @Test
    void processPayment_whenPaymentFails_shouldFailPaymentAndThrowException() {
        when(orderService.findOrderById(any(UUID.class))).thenReturn(orderDto);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment original = invocation.getArgument(0);
            Payment copy = new Payment();
            copy.setId(original.getId());
            copy.setOrderId(original.getOrderId());
            copy.setAmount(original.getAmount());
            copy.setStatus(original.getStatus());
            copy.setCreatedAt(original.getCreatedAt());
            copy.setCompletedAt(original.getCompletedAt());
            return copy;
        });
        when(paymentProcessor.processPayment(any(UUID.class), any(BigDecimal.class))).thenReturn(false);

        assertThrows(PaymentFailedException.class, () -> {
            paymentService.processPayment(paymentRequestDto);
        });

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(2)).save(paymentCaptor.capture());

        Payment initialPayment = paymentCaptor.getAllValues().get(0);
        Payment finalPayment = paymentCaptor.getAllValues().get(1);

        assertEquals(PaymentStatus.PROCESSING, initialPayment.getStatus());
        assertEquals(PaymentStatus.FAILED, finalPayment.getStatus());

        verify(eventPublisher, never()).publishPaymentCompleted(any());
        verify(paymentMapper, never()).toDto(any());
    }
}