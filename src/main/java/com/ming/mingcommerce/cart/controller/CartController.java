package com.ming.mingcommerce.cart.controller;

import com.ming.mingcommerce.cart.model.CartProductQuantityUpdate;
import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        int cartLineCount = cartService.addProduct(currentMember, request);
        return new ResponseEntity<>(Map.of("cartLineCount", cartLineCount), HttpStatus.OK);
    }

    /**
     * 장바구니에 담긴 상품의 수량(quantity)를 수정한다.
     *
     * @param authentication
     * @param update         상품 고유 ID 와 업데이트할 수량
     */
    @PutMapping
    public ResponseEntity<?> updateProductQuantity(Authentication authentication,
                                                   @RequestBody CartProductQuantityUpdate update) {
        if (!(authentication.getPrincipal() instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }
        int cartLineCount = cartService.updateQuantity(currentMember, update);

        return new ResponseEntity<>(Map.of("cartLineCount", cartLineCount), HttpStatus.OK);
    }
}
