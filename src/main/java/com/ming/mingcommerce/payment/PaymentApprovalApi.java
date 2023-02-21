package com.ming.mingcommerce.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.model.PaymentApprovalResponse;

public interface PaymentApprovalApi {
    PaymentApprovalResponse processPay(PaymentApprovalRequest request) throws JsonProcessingException;
}
