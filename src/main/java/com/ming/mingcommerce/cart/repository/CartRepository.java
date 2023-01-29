package com.ming.mingcommerce.cart.repository;

import com.ming.mingcommerce.cart.entity.Cart;
import com.ming.mingcommerce.security.CurrentMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    @Query("select c from Cart c where c.member.uuid = :uuid")
    Optional<Cart> findByMemberId(@Param("uuid") String uuid);

    default Cart findByMember(CurrentMember currentMember) {
        String uuid = currentMember.getUuid();
        return findByMemberId(uuid)
                .orElseGet(Cart::new);
    }
}
