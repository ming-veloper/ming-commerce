package com.ming.mingcommerce.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.model.PaymentApprovalResponse;
import com.ming.mingcommerce.payment.service.PaymentService;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * 결제를 진행한다.
     */
    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody PaymentApprovalRequest request, Authentication authentication) throws JsonProcessingException {
        if (!(authentication.getPrincipal() instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }
        PaymentApprovalResponse response = paymentService.pay(request, currentMember);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
