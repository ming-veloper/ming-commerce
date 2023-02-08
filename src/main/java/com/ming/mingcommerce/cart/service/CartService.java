package com.ming.mingcommerce.cart.service;

import com.ming.mingcommerce.cart.entity.Cart;
import com.ming.mingcommerce.cart.model.CartProductDeleteRequest;
import com.ming.mingcommerce.cart.model.CartProductQuantityUpdate;
import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.cart.model.CartProductDTO;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.cart.vo.CartLine;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
     * @param currentMember 현재 컨텍스트에서 인증된 유저
     * @param request       장바구니에 담을 productId 와 quantity
     * @return 장바구니 상품의 개수
     */
    @Transactional
    public int addProduct(CurrentMember currentMember, CartProductRequest request) {
        Cart cart = cartRepository.findAllByMember(currentMember);
        // 처음 카트에 담는 멤버라면, 멤버 세팅
        if (cart.getMember() == null) {
            Member member = memberRepository.findMemberByEmail(currentMember.getEmail());
            cart.setMember(member);
        }
        // 존재하는 상품인지 검증
        String productId = request.getProductId();
        Long quantity = request.getQuantity();

        Product product = productRepository.findProductById(productId);

        // 장바구니에 해당 상품이 이미 담겨있는지 알아보기 위한 predicate
        Predicate<CartLine> predicate = cartLine -> Objects.equals(cartLine.getProductId(), productId);

        // 장바구니에 상품이 이미 존재한다면 dirty checking 으로 업데이트하고, 새로운 상품이라면 cartline 객체를 새로 생성하여 저장.
        Optional<CartLine> optionalCartLine = cart.getCartLines()
                .stream()
                .filter(predicate)
                .findFirst();
        // case 1. 처음 담는 상품인 경우
        if (optionalCartLine.isEmpty()) {
            CartLine cartLine = CartLine.createCartLine(product, quantity);
            cart.getCartLines().add(cartLine);
        }
        else {
            CartLine cartLine = optionalCartLine.get();
            // case 2. 이전에 담았던 상품이고 deleted 가 true 인 상품인 경우
            if (cartLine.isDeleted()) {
                // 장바구니에서 삭제했었던 상품을 다시 담는다
                cartLine.putBack();
                cartLine.updateQuantity(request.getQuantity());
            }
            // case 3. 이전에 담았던 상품이고 deleted 가 false 인 상품의 경우
            else {
                cartLine.plusQuantity(request.getQuantity());
            }
        }

        Cart savedCart = cartRepository.saveAndFlush(cart);

        return savedCart.getCartLines().size();
    }

    @Transactional
    public int updateQuantity(CurrentMember currentMember, CartProductQuantityUpdate update) {
        Cart cart = cartRepository.findByMember(currentMember);

        String productId = update.getProductId();
        Long updateQuantity = update.getQuantity();
        // 장바구니에서 수량 업데이트를 하고자하는 카트 상품을 찾는다
        Predicate<CartLine> predicate = cartLine -> Objects.equals(cartLine.getProductId(), productId);

        CartLine cartLine = cart.getCartLines()
                .stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        // 수량 업데이트
        cartLine.updateQuantity(updateQuantity);

        Cart savedCart = cartRepository.saveAndFlush(cart);
        Predicate<CartLine> isDeletedPredicate = cl -> !cl.isDeleted();
        return savedCart.getCartLines().stream().filter(isDeletedPredicate).toList().size();
    }

    /**
     * @param currentMember
     * @param deleteRequest
     * @return 장바구니에 담긴 상품의 수
     */
    @Transactional
    public int deleteProduct(CurrentMember currentMember, CartProductDeleteRequest deleteRequest) {
        Cart cart = cartRepository.findByMember(currentMember);
        String productId = deleteRequest.getProductId();

        Predicate<CartLine> predicate = cartLine -> Objects.equals(cartLine.getProductId(), productId);
        CartLine cartLine = cart.getCartLines()
                .stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        // 상품 삭제
        cartLine.delete();

        Cart savedCart = cartRepository.saveAndFlush(cart);
        Predicate<CartLine> isDeletedPredicate = cl -> !cl.isDeleted();
        return savedCart.getCartLines().stream().filter(isDeletedPredicate).toList().size();
    }

    /**
     * 상품 조회
     *
     * @param currentMember
     */
    public List<CartProductDTO> findProducts(CurrentMember currentMember) {
        return cartRepository.getCartProductResponse(currentMember.getEmail());
    }
}
