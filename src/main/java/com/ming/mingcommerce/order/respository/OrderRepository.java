package com.ming.mingcommerce.order.respository;

import com.ming.mingcommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
