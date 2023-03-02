package com.ming.mingcommerce.order.service;

import com.ming.mingcommerce.cart.model.CartLineDTO;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.model.OrderDetail;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.model.OrderResponse;
import com.ming.mingcommerce.order.respository.OrderRepository;
import com.ming.mingcommerce.order.vo.OrderLine;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    /**
     * 주문(Order) 과 주문 라인(OrderLine)을 생성하여 저장한 뒤 주문 id 와 총 주문 금액을 반환한다.
     *
     * @param member
     * @param orderRequest
     * @return orderId, amount, orderName
     */
    @Transactional
    public OrderResponse order(Member member, OrderRequest orderRequest) {

        // 주문 라인 생성
        List<OrderLine> orderLines = createOrderLines(orderRequest.getCartLineUuidList());
        // 주문 생성
        Order order = Order.create(member, orderLines);
        // 주문 저장
        orderRepository.save(order);

        return new OrderResponse(order.getOrderId(), order.getTotalAmount(), order.getOrderName(), null);
    }

    private List<OrderLine> createOrderLines(List<String> cartLineUuidList) {
        List<CartLineDTO> cartLineDTOList = cartRepository.getCartLineDTO(cartLineUuidList);
        return cartLineDTOList.stream().map(OrderLine::create).toList();
    }

    // 주문 조회시 해당 주문 조회 권한이 있는 요청인지 검증
    private void validate(CurrentMember currentMember, String orderId) {
        Order order = orderRepository.findOrderByOrderId(orderId);
        if (!Objects.equals(order.getMember().getEmail(), currentMember.getEmail())) {
            throw new IllegalArgumentException("해당 주문을 조회할 수 있는 사용자가 아닙니다.");
        }
    }

    // 주문 조회
    public OrderResponse getOrder(String orderId, CurrentMember currentMember) {
        // 현재 요청의 사용자가 해당 주문을 조회할 수 있는지 검증
        validate(currentMember, orderId);
        Order order = orderRepository.findOrderByOrderId(orderId);
        return modelMapper.map(order, OrderResponse.class);
    }

    // orderId 에 해당하는 주문 상세 조회
    public List<OrderDetail> getOrderDetail(String orderId, CurrentMember currentMember) {
        // 현재 요청의 사용자가 해당 주문을 조회할 수 있는지 검증
        validate(currentMember, orderId);
        return orderRepository.getOrderDetail(orderId);
    }

    // 사용자 주문 조회. 최대 5개의 최신 주문을 조회한다.
    public List<OrderResponse> getMyOrder(CurrentMember currentMember, Pageable pageable) {
        // Member 엔티티 타입으로 형변환
        Member member = modelMapper.map(currentMember, Member.class);
        return orderRepository.getMyOrder(member, pageable);
    }
}
