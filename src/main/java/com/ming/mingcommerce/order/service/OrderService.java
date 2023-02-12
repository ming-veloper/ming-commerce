package com.ming.mingcommerce.order.service;

import com.ming.mingcommerce.cart.model.CartLineDTO;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.model.OrderResponse;
import com.ming.mingcommerce.order.respository.OrderRepository;
import com.ming.mingcommerce.order.vo.OrderLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    /**
     * 주문(Order) 과 주문 라인(OrderLine)을 생성하여 저장한 뒤 주문 id 와 총 주문 금액을 반환한다.
     *
     * @param member
     * @param orderRequest
     * @return
     */
    @Transactional
    public OrderResponse order(Member member, OrderRequest orderRequest) {
        // 주문 생성
        Order order = Order.create(member);

        // 주문 라인 생성
        List<CartLineDTO> cartLineDTOList = cartRepository.getCartLineDTO(orderRequest.getCartLineUuidList());
        cartLineDTOList.stream().map(OrderLine::create).forEach(order::addOrderLine);

        // 총 주문 금액 계산
        Double amount = order.calculateTotalAmount();
        // 주문 이름 추출
        String orderName = order.extractOrderName();

        // 주문 저장
        orderRepository.save(order);

        return new OrderResponse(order.getOrderId(), amount, orderName);
    }
}
