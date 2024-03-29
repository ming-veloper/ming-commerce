package com.ming.mingcommerce.order.respository;

import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.model.MyOrderModel;
import com.ming.mingcommerce.order.model.OrderDetail;
import com.ming.mingcommerce.order.model.OrderProductDetail;
import com.ming.mingcommerce.order.model.ProductDetail;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    default Order findByOrderId(String orderId) {
        return findById(orderId).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * 주문 상세를 위한 주문상품 정보를 조회한다. 상품아이디, 상품이름, 썸네일, 주문상품가격, 주문상품수량을 반환한다.
     *
     * @param orderId
     * @return 상품아이디, 상품이름, 썸네일, 주문상품가격, 주문상품수량을 반환한다.
     */
    @Query("""
            SELECT new com.ming.mingcommerce.order.model.ProductDetail(
                p.productId, p.productName, p.thumbnailImageUrl, ol.price, ol.quantity
            )
            FROM Order o
                JOIN Product p
                JOIN o.orderLineList ol
                    ON ol.productId = p.productId
                        WHERE o.orderId = :orderId
            """)
    List<ProductDetail> getOrderProductDetail(String orderId);

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
     * 주문아이디, 주문이름, 주문총금액, 주문썸네일, 주문일자를 반환합니다.
     *
     * @param memberUuid 사용자의 uuid 입니다.
     * @param pageable   페이징 객체를 처리하기 위한 객체입니다.
     * @return 사용자의 주문 상품 목록
     */
    @Query("""
            SELECT new com.ming.mingcommerce.order.model.MyOrderModel(
                o.orderId, o.orderName, o.totalAmount, o.orderThumbnailUrl, o.modifiedDate
            ) FROM Order o
                WHERE o.member.uuid = :memberUuid
                    AND o.orderStatus = 'COMPLETE'
            """)
    Page<MyOrderModel> getMyOrderList(String memberUuid, Pageable pageable);

    // 주문 상세 조회
    default OrderProductDetail getMyOrderProductDetail(String orderId) {
        Order order = findByOrderId(orderId);
        OrderDetail orderDetail = order.toOrderDetail(order);
        List<ProductDetail> productDetailList = getOrderProductDetail(orderId);
        return new OrderProductDetail(orderDetail, productDetailList);
    }


}
