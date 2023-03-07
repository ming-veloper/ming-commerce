package com.ming.mingcommerce.order.respository;

import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.model.MyOrderProjectionModel;
import com.ming.mingcommerce.order.model.OrderDetail;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    default Order findOrderByOrderId(String orderId) {
        return findById(orderId).orElseThrow(EntityNotFoundException::new);
    }

    @Query(
            """
                    SELECT new com.ming.mingcommerce.order.model.OrderDetail(
                        p.productId, p.productName, p.thumbnailImageUrl, ol.price, ol.quantity
                    )
                    FROM Order o
                    JOIN Product p
                    JOIN o.orderLineList ol
                        ON ol.productId = p.productId
                        WHERE o.orderId = :orderId
                    """
    )
    List<OrderDetail> getOrderDetail(String orderId);

    /**
     * 사용자의 주문 목록을 조회합니다.
     * 하나의 주문 안에는 여러개의 주문 상품이 포함 되어 있습니다.
     * <pre>
     *     {
     *         order: {
     *             ...,
     *             orderLineList: [
     *                  {...}, {...}, {...}
     *             ]
     *         }
     *     }
     * </pre>
     * 주문 상품들 중에서 제일 첫 번째 주문 상품의 상품 이미지를 주문 대표 이미지로 설정해야 합니다.
     * (사용자 주문 목록 조회에 노출하기 위한.)
     * <p>
     * 이를 위해서는 JPA 를 사용하는 것 보다는 NativeQuery 를 사용하는것이 적당하다고 판단되어 해당 메소드에 적용하였습니다.
     * <p>
     * 또한 네이티브 쿼리를 사용하게 되면 기존의 DTO 로 취급하던 클래스 객체로는 프로젝션이 불가능하기 때문에 규약에 따라 인터페이스를 정의하여 사용하였습니다.
     *
     * @param memberUuid 사용자의 uuid 입니다.
     * @param pageable   페이징 객체를 처리하기 위한 객체입니다.
     * @return 사용자의 주문 상품 목록
     */
    @Query(value = """
            SELECT po.order_id as orderId,
                   po.total_amount as totalAmount,
                   po.order_name as orderName,
                   p.thumbnail_image_url as thumbnailImageUrl
            FROM purchase_order po
                     JOIN order_line ol
                          ON po.order_id = ol.order_id
                     JOIN product p
                          ON p.product_id = ol.product_id
                              AND ol.line_idx = (SELECT min(ol.line_idx)
                                                 FROM order_line ol
                                                 WHERE ol.order_id = po.order_id)
            WHERE po.order_status = 'COMPLETE'
              AND po.member_uuid = :memberUuid""", nativeQuery = true,
            countQuery = """
                    select count(*)
                    FROM purchase_order po
                             JOIN
                         order_line ol
                         ON po.order_id = ol.order_id
                             JOIN
                         product p
                         ON p.product_id = ol.product_id
                             AND ol.line_idx = (SELECT min(ol.line_idx)
                                                FROM order_line ol
                                                WHERE ol.order_id = po.order_id)
                    WHERE po.order_status = 'COMPLETE'
                      AND po.member_uuid = :memberUuid""")
    Page<MyOrderProjectionModel> getMyOrder(String memberUuid, Pageable pageable);


}
