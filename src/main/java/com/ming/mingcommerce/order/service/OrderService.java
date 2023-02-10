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
     * @param orderRequestList
     * @return
     */
    @Transactional
    public OrderResponse order(Member member, List<OrderRequest> orderRequestList) {
        // 주문 생성
        Order order = Order.create(member);

        // 주문 라인 생성
        orderRequestList.forEach(orderRequest -> {
            OrderLine orderLine = OrderLine.create(orderRequest.getCartLindUuid());
            order.getOrderLineList().add(orderLine);
        });

        // 총 주문 금액 계산
        Map<?, ?> result = calculateTotalAmount(orderRequestList);

        // 주문 저장
        orderRepository.save(order);

        return new OrderResponse(order.getOrderId(), Double.parseDouble(result.get("totalAmount").toString()),result.get("productNameMsg").toString());
    }

    // 주문 총 금액 계산
    private Map<?, ?> calculateTotalAmount(List<OrderRequest> orderRequestList) {
        List<String> cartLineUuidList = orderRequestList.stream().map(OrderRequest::getCartLindUuid).toList();
        // 상품 가격, 상품 수량이 담긴 CartLineDTO
        List<CartLineDTO> cartLineDTOList = cartRepository.getCartLineDTO(cartLineUuidList);

        // 총 합계를 계산
        Double totalAmount = cartLineDTOList.stream()
                .mapToDouble((cl) -> cl.getPrice() * cl.getQuantity()).sum();

        // 주문 이름을 구한다
        String productNameMsg = "";
        String firstProductName = cartLineDTOList.get(0).getProductName();
        int size = cartLineUuidList.size() - 1;
        productNameMsg += firstProductName + " 외 " + size + "건";

        return Map.of("totalAmount", totalAmount, "productNameMsg", productNameMsg);

    }
}
