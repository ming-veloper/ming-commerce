package com.ming.mingcommerce.cart.controller;

import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    /**
     * 장바구니에 상품 담기. 상품 PK 인 productId 와 상품 개수인 quantity 를 쿼리 파리미터로 한다. quantity 는 기본값 1로 세팅 된다.
     *
     * @param authentication 현재 SecurityContext 의 인증 객체
     * @param request        상품 고유 ID 와 장바구니에 담을 상품의 개수
     */
    @PostMapping
    public ResponseEntity<?> addProduct(Authentication authentication,
                                        @RequestBody CartProductRequest request) {
        if (!(authentication.getPrincipal() instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }
        cartService.addProduct(currentMember, request);
        return ResponseEntity.ok().build();
    }
}
