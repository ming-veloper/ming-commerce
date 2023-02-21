package com.ming.mingcommerce.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.model.PaymentApprovalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class TossPaymentApprovalApi implements PaymentApprovalApi {
    private final ObjectMapper objectMapper;
    private final String tossSecretKey;

    @Override
    public PaymentApprovalResponse processPay(PaymentApprovalRequest request) throws JsonProcessingException {
        // 헤더
        HttpHeaders headers = new HttpHeaders();
        String header = Base64.getEncoder().encodeToString(tossSecretKey.getBytes());
        headers.setBasicAuth(header);
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 데이터
        HttpEntity<PaymentApprovalRequest> requestEntity = new HttpEntity<>(request, headers);
        // 요청
        RestTemplate restTemplate = new RestTemplate();
        String responseStr = restTemplate.postForObject("https://api.tosspayments.com/v1/payments/confirm", requestEntity, String.class);
        return objectMapper.readValue(responseStr, PaymentApprovalResponse.class);
    }
}
