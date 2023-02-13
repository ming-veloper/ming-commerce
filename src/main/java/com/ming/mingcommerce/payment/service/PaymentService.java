package com.ming.mingcommerce.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.payment.model.PayApprovalResponse;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {
    private final ObjectMapper objectMapper;

    public PayApprovalResponse pay(PaymentApprovalRequest request) throws JsonProcessingException {
        // 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg=="); // 토스 연동 테스트용 인증 토큰
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 데이터
        HttpEntity<PaymentApprovalRequest> requestEntity = new HttpEntity<>(request, headers);
        // 요청
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.postForObject("https://api.tosspayments.com/v1/payments/confirm", requestEntity, String.class);

        // 응답 객체로 변환
        return objectMapper.readValue(response, PayApprovalResponse.class);
    }
}
