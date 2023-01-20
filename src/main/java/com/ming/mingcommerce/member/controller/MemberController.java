package com.ming.mingcommerce.member.controller;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.exception.MemberException;
import com.ming.mingcommerce.member.model.MemberModel;
import com.ming.mingcommerce.member.model.RegisterRequest;
import com.ming.mingcommerce.member.model.RegisterResponse;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.member.service.MemberService;
import com.ming.mingcommerce.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

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
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        String email = currentUser.getEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException.MemberEmailNotFoundException("Email cannot found: " + email));
        MemberModel memberModel = modelMapper.map(member, MemberModel.class);

        return new ResponseEntity<>(Map.of("result", memberModel), HttpStatus.OK);
    }
}
