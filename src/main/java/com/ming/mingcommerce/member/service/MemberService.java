package com.ming.mingcommerce.member.service;

import com.ming.mingcommerce.member.model.RegisterRequest;
import com.ming.mingcommerce.member.model.RegisterResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        return null;
    }
}
