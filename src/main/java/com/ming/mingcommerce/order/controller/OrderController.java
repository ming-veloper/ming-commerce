package com.ming.mingcommerce.order.controller;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.model.OrderDetail;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.model.OrderResponse;
import com.ming.mingcommerce.order.service.OrderService;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 주문 조회
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
     * 주문 상세를 조회한다
     *
     * @param authentication
     * @param orderId
     * @return orderDetail
     */
    @GetMapping("/order-detail")
    public ResponseEntity<?> getOrderDetail(Authentication authentication, @Param("orderId") String orderId) {
        if (!((authentication.getPrincipal()) instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }
        List<OrderDetail> result = orderService.getOrderDetail(orderId, currentMember);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
