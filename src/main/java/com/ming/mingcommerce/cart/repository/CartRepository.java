package com.ming.mingcommerce.cart.repository;

import com.ming.mingcommerce.cart.entity.Cart;
import com.ming.mingcommerce.cart.model.CartLineDTO;
import com.ming.mingcommerce.cart.model.CartProductDTO;
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
            JOIN c.cartLines cl
            WHERE c.member.uuid = :uuid
                AND cl.deleted = false
            """)
    Optional<Cart> findByMemberId(@Param("uuid") String uuid);

    default Cart findByMember(CurrentMember currentMember) {
        String uuid = currentMember.getUuid();
        return findByMemberId(uuid)
                .orElseGet(Cart::new);
    }


    @Query("""
            SELECT new com.ming.mingcommerce.cart.model.CartProductDTO(
                cl.uuid, p.productId, p.price, cl.quantity, p.productName, p.thumbnailImageUrl, cl.createdDate, cl.modifiedDate
            )
            FROM Cart c
            JOIN c.cartLines cl
            JOIN Product p
                ON p.productId = cl.productId
            WHERE cl.deleted = false
                AND c.member.email = :email
            """)
    List<CartProductDTO> getCartProductResponse(String email);

    @Query("""
            SELECT new com.ming.mingcommerce.cart.model.CartLineDTO(
                cl.uuid ,p.productId, cl.quantity, cl.price, p.productName, cl.deleted
            )
            FROM Cart c
            JOIN c.cartLines cl
            JOIN Product p
                ON cl.productId = p.productId
                WHERE cl.uuid IN (:cartLineUuidList)
            """)
    List<CartLineDTO> getCartLineDTO(@Param("cartLineUuidList") List<String> cartLineUuidList);

    @Query("""
            SELECT cl.deleted
            FROM Cart c
            JOIN c.cartLines cl
                WHERE cl.uuid IN (:cartLineUuidList)
            """)
    List<Boolean> isCartLineUuidDeleted(@Param("cartLineUuidList") List<String> cartLineUuidList);

    @Query("""
            SELECT p.thumbnailImageUrl
            FROM Cart c
            JOIN c.cartLines cl
            JOIN Product p
                ON p.productId = cl.productId
                where cl.uuid = (:firstCartLineUuid)
            """)
    String getRepresentProductImageUrl(String firstCartLineUuid);
}
