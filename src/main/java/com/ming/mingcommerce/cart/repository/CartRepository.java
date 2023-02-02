package com.ming.mingcommerce.cart.repository;

import com.ming.mingcommerce.cart.entity.Cart;
import com.ming.mingcommerce.cart.model.CartProductResponse;
import com.ming.mingcommerce.security.CurrentMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    @Query("""
            SELECT c
            FROM Cart c
            JOIN c.productList pl
            WHERE c.member.uuid = :uuid
                AND pl.deleted = false
            """)
    Optional<Cart> findByMemberId(@Param("uuid") String uuid);

    default Cart findByMember(CurrentMember currentMember) {
        String uuid = currentMember.getUuid();
        return findByMemberId(uuid)
                .orElseGet(Cart::new);
    }


    @Query("""
            SELECT new com.ming.mingcommerce.cart.model.CartProductResponse(
                p.productId, p.price, cl.quantity, p.productName, p.thumbnailImageUrl, cl.createdDate, cl.modifiedDate
            )
            FROM Cart c
            JOIN c.productList cl
            JOIN Product p
                ON p.productId = cl.productId
            WHERE cl.deleted = false
                AND c.member.email = :email
            """)
    List<CartProductResponse> getCartProductResponse(String email);

}
