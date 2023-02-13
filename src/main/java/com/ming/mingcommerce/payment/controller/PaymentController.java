package com.ming.mingcommerce.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.respository.OrderRepository;
import com.ming.mingcommerce.payment.model.PayApprovalResponse;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    /**
     * 결제를 진행한다.
     */
    @PostMapping("/pay")
    public ResponseEntity<?> pay(PaymentApprovalRequest request) throws JsonProcessingException {
        // 주문 금액 검증
        Order order = orderRepository.findOrderByOrderId(request.getOrderId());
        order.validateAmount(request.getAmount());
        PayApprovalResponse response = paymentService.pay(request);
        // 주문 상태 바꾸기 PENDING -> COMPLETE
        order.successPay();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
