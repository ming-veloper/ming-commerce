package com.ming.mingcommerce.order.controller;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.model.OrderResponse;
import com.ming.mingcommerce.order.service.OrderService;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @return
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
}
