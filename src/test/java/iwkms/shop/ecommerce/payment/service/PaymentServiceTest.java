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
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    private PaymentService paymentService;

    private OrderDto orderDto;
    private PaymentRequestDto paymentRequestDto;
    private PaymentDto paymentDto;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
            paymentRepository,
            orderService,
            paymentProcessor,
            eventPublisher,
            paymentMapper
        );

        UUID orderId = UUID.randomUUID();
        orderDto = new OrderDto(orderId, UUID.randomUUID(), null, new BigDecimal("100.00"), "", LocalDateTime.now(), null);
        paymentRequestDto = new PaymentRequestDto(orderId);

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setOrderId(orderId);
        payment.setAmount(orderDto.totalPrice());
        payment.setStatus(PaymentStatus.PROCESSING);
    }

    @Test
    void processPayment_WhenSuccessful_ShouldReturnCompletedPayment() {
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

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);

        verify(paymentRepository, times(2)).save(paymentCaptor.capture());

        Payment initialPayment = paymentCaptor.getAllValues().get(0);
        Payment finalPayment = paymentCaptor.getAllValues().get(1);

        assertThat(initialPayment.getStatus()).isEqualTo(PaymentStatus.PROCESSING);
        assertThat(finalPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(finalPayment.getCompletedAt()).isNotNull();

        verify(eventPublisher).publishPaymentCompleted(any(PaymentCompletedEvent.class));
    }

    @Test
    void processPayment_WhenFailed_ShouldThrowException() {
        when(orderService.findOrderById(any(UUID.class))).thenReturn(orderDto);
        when(paymentProcessor.processPayment(any(UUID.class), any(BigDecimal.class))).thenReturn(false);

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

        assertThatThrownBy(() -> paymentService.processPayment(paymentRequestDto))
            .isInstanceOf(PaymentFailedException.class)
            .hasMessageContaining("Payment provider declined the transaction");

        verify(paymentRepository, times(2)).save(paymentCaptor.capture());
        verify(eventPublisher, never()).publishPaymentCompleted(any());
        
        Payment savedPayment = paymentCaptor.getValue();
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }
}