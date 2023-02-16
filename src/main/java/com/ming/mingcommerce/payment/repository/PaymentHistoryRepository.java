package com.ming.mingcommerce.payment.repository;

import com.ming.mingcommerce.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, String> {
}
