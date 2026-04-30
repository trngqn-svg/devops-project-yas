package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.mapper.OrderMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .checkoutId("checkout-123")
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    @Test
    void testFindOrderByCheckoutId_Success() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.of(order));

        Order result = orderService.findOrderByCheckoutId("checkout-123");

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCheckoutId()).isEqualTo("checkout-123");
    }

    @Test
    void testFindOrderByCheckoutId_NotFound() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("checkout-123"));
    }

    @Test
    void testRejectOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "Out of stock");

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("Out of stock");
        verify(orderRepository).save(order);
    }

    @Test
    void testRejectOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.rejectOrder(1L, "Out of stock"));
    }

    @Test
    void testAcceptOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(orderRepository).save(order);
    }

    @Test
    void testAcceptOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.acceptOrder(1L));
    }

    @Test
    void testGetOrderWithItemsById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void testGetOrderWithItemsById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void testGetLatestOrders_CountZero_ReturnsEmpty() {
        List<OrderBriefVm> result = orderService.getLatestOrders(0);
        assertThat(result).isEmpty();
    }

    @Test
    void testGetLatestOrders_Empty_ReturnsEmpty() {
        when(orderRepository.getLatestOrders(any())).thenReturn(List.of());
        List<OrderBriefVm> result = orderService.getLatestOrders(5);
        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateOrderPaymentStatus_Success() {
        PaymentOrderStatusVm paymentOrderStatusVm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(1L)
                .paymentStatus("PENDING")
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(paymentOrderStatusVm);

        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void testUpdateOrderPaymentStatus_Completed_SetsStatusPaid() {
        PaymentOrderStatusVm paymentOrderStatusVm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(1L)
                .paymentStatus("COMPLETED")
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        orderService.updateOrderPaymentStatus(paymentOrderStatusVm);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void testFindOrderVmByCheckoutId_Success() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-123");

        assertThat(result.id()).isEqualTo(1L);
    }
}
