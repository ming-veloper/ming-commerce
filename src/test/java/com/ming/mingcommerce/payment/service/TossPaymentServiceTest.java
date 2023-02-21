package com.ming.mingcommerce.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ming.mingcommerce.BaseServiceTest;
import com.ming.mingcommerce.cart.entity.Cart;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.cart.vo.CartLine;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.entity.OrderStatus;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.model.OrderResponse;
import com.ming.mingcommerce.order.respository.OrderRepository;
import com.ming.mingcommerce.order.service.OrderService;
import com.ming.mingcommerce.order.vo.OrderLine;
import com.ming.mingcommerce.payment.PaymentApprovalApi;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.model.PaymentApprovalResponse;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.security.CurrentMember;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TossPaymentServiceTest extends BaseServiceTest {
    @MockBean
    PaymentApprovalApi paymentApprovalApi;
    @Autowired
    PaymentService paymentService;


    @Autowired
    OrderService orderService;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional
    @DisplayName("토스 결제 승인에 성공한다")
    void approvePay() throws JsonProcessingException {
        // given
        // 상품과 멤버 저장 후 카트에 상품을 담아 주문한다
        CurrentMember currentMember = saveMember();
        List<Product> products = saveProduct();
        Member member = modelMapper.map(currentMember, Member.class);
        Cart cart = new Cart("test-cart-id", List.of(CartLine.createCartLine(products.get(0), 10L)), member);
        cartRepository.save(cart);

        OrderResponse orderResponse = orderService.order(member, new OrderRequest(List.of(cart.getCartLines().get(0).getUuid())));
        String orderId = orderResponse.getOrderId();
        Double amount = orderResponse.getAmount();
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                .paymentKey("test-payment-key")
                .amount(amount)
                .orderId(orderId).build();
        PaymentApprovalResponse response = new PaymentApprovalResponse(orderId,
                "testOrder",
                "2023-02-21", "2023-02-21",
                "KRW", amount, "카드");
        when(paymentApprovalApi.processPay(any(PaymentApprovalRequest.class))).thenReturn(response);
        // when
        paymentService.pay(request, currentMember);
        // then
        // 주문 상태가 PENDING -> COMPLETE 로 변경된다
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE);
        // 주문이 완료되었으므로 카트라인의 삭제 여부가 true 로 변경된다
        Order savedOrder = orderRepository.findOrderByOrderId(orderId);
        List<String> cartLineUuidList = savedOrder.getOrderLineList().stream().map(OrderLine::getCartLineUuid).toList();
        List<Boolean> cartLineDTOList = cartRepository.isCartLineUuidDeleted(cartLineUuidList);
        assertThat(cartLineDTOList.stream().allMatch((Boolean::booleanValue))).isTrue();
    }
}