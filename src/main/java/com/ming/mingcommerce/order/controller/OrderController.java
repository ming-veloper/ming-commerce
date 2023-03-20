package com.ming.mingcommerce.order.controller;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.model.OrderProductDetail;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.model.OrderResponse;
import com.ming.mingcommerce.order.service.OrderService;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    /**
     * 장바구니에 있는 상품을 주문한다. 장바구니에 담긴 각 상품은 uuid 로 구분되며, 주문할 카트 상품의 uuid 를 리스트 형식으로 요청한다.
     *
     * @param authentication
     * @param orderRequest
     * @return orderResponse
     */
    @PostMapping("/order")
    public ResponseEntity<?> order(Authentication authentication, @RequestBody OrderRequest orderRequest) {
        if (!(authentication.getPrincipal() instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }
        Member member = modelMapper.map(currentMember, Member.class);
        OrderResponse orderResponse = orderService.order(member, orderRequest);

        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    /**
     * 주문 아이디에 해당하는 주문을 조회한다.
     *
     * @param authentication
     * @param orderId
     * @return orderResponse
     */
    @GetMapping("/order")
    public ResponseEntity<?> getOrder(Authentication authentication, @Param("orderId") String orderId) {
        if (!(authentication.getPrincipal() instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }
        OrderResponse response = orderService.getOrder(orderId, currentMember);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 사용자의 주문을 조회한다. 최신 주문이 가장 위에 나온다.
     *
     * @param authentication
     * @return orderId, amount, orderName, orderStatus 로 이루어진 주문 조회 객체
     */
    @GetMapping("/my-order")
    public ResponseEntity<?> getMyOrder(Authentication authentication, Pageable pageable) {
        if (!(authentication.getPrincipal() instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }
        var myOrders = orderService.getMyOrderList(currentMember, pageable);
        return new ResponseEntity<>(myOrders, HttpStatus.OK);
    }


    /**
     * 주문 상세를 조회한다.
     *
     * @param authentication
     * @param orderId
     * @return 주문 정보(주문이름, 주문총액, 주문날짜)와 주문상품 정보(상품아이디, 상품이름, 썸네일, 가격, 주문수량)를 반환한다.
     */
    @GetMapping("/order-detail")
    public ResponseEntity<?> getOrderDetail(Authentication authentication, @Param("orderId") String orderId) {
        if (!((authentication.getPrincipal()) instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }
        OrderProductDetail result = orderService.getOrderProductDetail(orderId, currentMember);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
