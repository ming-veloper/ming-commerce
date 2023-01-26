package com.ming.mingcommerce.cart.controller;

import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * @param productId      상품 고유 ID
     * @param quantity       (optional) 장바구니에 담을 상품의 개수. 기본값 1.
     * @return 상품 담기 성공 여부. (true or false)
     */
    @GetMapping
    public ResponseEntity<?> addProduct(Authentication authentication,
                                        @RequestParam String productId,
                                        @RequestParam(required = false, defaultValue = "1") Long quantity) {
        if (!(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            throw new IllegalArgumentException();
        }
        cartService.addProduct(currentUser, quantity, productId);
        return ResponseEntity.ok().build();
    }
}
