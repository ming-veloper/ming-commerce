package com.ming.mingcommerce.order.respository;

import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.model.OrderDetail;
import jakarta.persistence.EntityNotFoundException;
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
}
