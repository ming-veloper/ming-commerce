package com.ming.mingcommerce.member.controller;

import com.ming.mingcommerce.member.exception.MemberException;
import com.ming.mingcommerce.member.model.RegisterRequest;
import com.ming.mingcommerce.member.model.RegisterResponse;
import com.ming.mingcommerce.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@Valid @RequestBody RegisterRequest registerRequest,
                                            BindingResult result) {
        if (result.hasErrors()) {
            throw new MemberException.MemberRegisterFailedException("invalid request data");
        }
        RegisterResponse response = memberService.register(registerRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
