package com.ming.mingcommerce.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ming.mingcommerce.cart.entity.Cart;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.cart.vo.CartLine;
import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.respository.OrderRepository;
import com.ming.mingcommerce.order.vo.OrderLine;
import com.ming.mingcommerce.payment.PaymentApprovalApi;
import com.ming.mingcommerce.payment.entity.PaymentHistory;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.model.PaymentApprovalResponse;
import com.ming.mingcommerce.payment.repository.PaymentHistoryRepository;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CartRepository cartRepository;
    private final PaymentApprovalApi tossPaymentApprovalApi;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PaymentApprovalResponse pay(PaymentApprovalRequest request, CurrentMember currentMember) throws JsonProcessingException {
        validateAmount(request);
        PaymentApprovalResponse response = tossPaymentApprovalApi.processPay(request);
        // 결제 히스토리 저장
        savePaymentHistory(response);
        Order order = orderRepository.findOrderByOrderId(response.getOrderId());
        // 주문 상태 바꾸기 PENDING -> COMPLETE
        successPay(order, currentMember);
        return response;
    }

    private void successPay(Order order, CurrentMember currentMember) {
        order.completePay();
        // 결제까지 완료되었으므로, 카트상품 삭제
        Cart cart = cartRepository.findByMember(currentMember);
        List<String> cartLineUuidList = order.getOrderLineList().stream().map(OrderLine::getCartLineUuid).toList();
        cart.getCartLines().stream().filter(cartLine -> cartLineUuidList.contains(cartLine.getUuid())).forEach(CartLine::delete);
    }

    // 결제 요청 금액과 주문 금액이 같은지 검증
    private void validateAmount(PaymentApprovalRequest request) {
        Order order = orderRepository.findOrderByOrderId(request.getOrderId());
        order.validateAmount(request.getAmount());
    }

    @Override
    public void savePaymentHistory(PaymentApprovalResponse response) {
        PaymentHistory history = modelMapper.map(response, PaymentHistory.class);
        paymentHistoryRepository.save(history);
    }
}
