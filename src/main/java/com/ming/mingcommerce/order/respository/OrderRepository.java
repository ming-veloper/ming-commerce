package com.ming.mingcommerce.order.respository;

import com.ming.mingcommerce.order.entity.Order;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    default Order findOrderByOrderId(String orderId) {
        return findById(orderId).orElseThrow(EntityNotFoundException::new);
    }
}
