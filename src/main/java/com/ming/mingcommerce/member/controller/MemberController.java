package com.ming.mingcommerce.member.controller;

import com.ming.mingcommerce.member.exception.MemberException;
import com.ming.mingcommerce.member.model.MemberModel;
import com.ming.mingcommerce.member.model.RegisterRequest;
import com.ming.mingcommerce.member.model.RegisterResponse;
import com.ming.mingcommerce.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerMember(@Valid @RequestBody RegisterRequest registerRequest,
                                                           BindingResult result) {
        if (result.hasErrors()) {
            throw new MemberException.MemberRegisterFailedException("invalid request data");
        }
        RegisterResponse response = memberService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/email-duplication-check")
    public ResponseEntity<?> isEmailDuplicated(@RequestParam String email) {
        boolean duplicatedEmail = memberService.isDuplicatedEmail(email);
        Map<String, Boolean> result = Map.of("isDuplicated", duplicatedEmail);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfo(Authentication authentication) {
        MemberModel memberModel = memberService.getMemberInfo(authentication);
        return new ResponseEntity<>(Map.of("result", memberModel), HttpStatus.OK);
    }
}
