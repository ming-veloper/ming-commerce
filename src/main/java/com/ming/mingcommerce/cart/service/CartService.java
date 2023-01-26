package com.ming.mingcommerce.cart.service;

import com.ming.mingcommerce.cart.entity.Cart;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.cart.vo.CartLine;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CartService {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    /**
     * 장바구니에 상품을 추가한다.
     *
     * @param currentUser
     * @param quantity
     * @param productId
     */
    @Transactional
    public void addProduct(CurrentUser currentUser, Long quantity, String productId) {

        Cart cart = cartRepository.findByMember(currentUser);
        // 처음 카트에 담는 멤버라면, 멤버 세팅
        if (cart.getMember() == null) {
            Member member = memberRepository.findMemberByEmail(currentUser.getEmail());
            cart.setMember(member);
        }
        // 존재하는 상품인지 검증
        Product product = productRepository.findProductById(productId);

        // 장바구니에 해당 상품이 이미 담겨있는지 알아보기 위한 predicate
        Predicate<CartLine> predicate = cartLine -> Objects.equals(cartLine.getProductId(), productId);

        // 장바구니에 상품이 이미 존재한다면 dirty checking 으로 업데이트하고, 새로운 상품이라면 cartline 객체를 새로 생성하여 저장.
        cart.getProductList()
                .stream()
                .filter(predicate)
                .findFirst()
                .ifPresentOrElse((cartLine) -> cartLine.plusQuantity(quantity),
                        () -> {
                            CartLine cartLine = CartLine.builder()
                                    .productId(productId)
                                    .price(product.getPrice())
                                    .quantity(quantity)
                                    .createdDate(LocalDateTime.now())
                                    .modifiedDate(LocalDateTime.now())
                                    .build();

                            cart.getProductList().add(cartLine);
                        });

        cartRepository.saveAndFlush(cart);
    }
}
