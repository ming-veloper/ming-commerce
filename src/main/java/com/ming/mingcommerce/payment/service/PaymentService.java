package com.ming.mingcommerce.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.model.PaymentApprovalResponse;
import com.ming.mingcommerce.security.CurrentMember;

public interface PaymentService {

    PaymentApprovalResponse pay(PaymentApprovalRequest request, CurrentMember member) throws JsonProcessingException;

    void savePaymentHistory(PaymentApprovalResponse response);
}
