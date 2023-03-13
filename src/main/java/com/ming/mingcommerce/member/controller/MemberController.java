package com.ming.mingcommerce.member.controller;

import com.ming.mingcommerce.member.exception.MemberException;
import com.ming.mingcommerce.member.model.*;
import com.ming.mingcommerce.member.service.MemberService;
import com.ming.mingcommerce.security.CurrentMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
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

    /**
     * 유저가 이메일 변경을 위해 변경하고 싶은 이메일을 전송한다. 이메일 확인을 위해 해당 이메일로 인증메일을 전송한다.
     *
     * @param request 변경을 요청하는 이메일
     * @return 상태코드 200 일시 성공적으로 이메일이 전송됨
     */
    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmailForChangeEmail(@RequestBody MemberEmailAuthenticationRequest request, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CurrentMember currentMember))
            throw new IllegalArgumentException();
        memberService.sendEmail(request.getEmail(), currentMember);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 이메일 인증 성공시 유저의 이메일을 변경한다.
     *
     * @param token 유저 인증을 위한 UUID 형식의 토큰
     * @param email 변경을 원하는 이메일
     * @return 변경된 이메일로 새로 발급한 access token 과 refresh token
     */
    @GetMapping("/change-email")
    public ResponseEntity<?> changeEmail(@Param("token") String token,
                                         @Param("email") String email) {
        JwtTokenModel tokenModel = memberService.changeEmail(token, email);
        // 새로운 액세스토큰과 리프레시 토큰 발급하여 반환
        return new ResponseEntity<>(tokenModel, HttpStatus.OK);
    }
}
