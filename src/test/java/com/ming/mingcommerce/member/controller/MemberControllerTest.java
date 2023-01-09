package com.ming.mingcommerce.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.model.RegisterRequest;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.member.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입을 한다")
    void register() throws Exception {
        RegisterRequest data = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("helloTester!@#")
                .memberName("tester")
                .build();

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists())
                .andDo(document("register-member",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("8~12개의 문자로 이루어진 비밀번호"),
                                fieldWithPath("memberName").description("2~20자 사이의 회원 이름")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("액세스토큰. 7일간 유효하다"),
                                fieldWithPath("refreshToken").description("리프레시토큰. 30일간 유효하다")
                        )
                ))

        ;
    }

    @Test
    @DisplayName("이메일 중복 여부를 체크한다")
    void checkEmailDuplication() throws Exception {
        mockMvc.perform(get("/api/members/email-duplication-check")
                        .param("email", "tester@gmail.com")
                )
                .andExpect(status().isOk())
                .andDo(document("check-email-duplication",
                        queryParameters(
                                parameterWithName("email").description("중복 여부를 체크할 이메일")
                        ),
                        responseFields(
                                fieldWithPath("isDuplicated").description("이메일 중복 여부")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void registerFail_DuplicatedEmail() throws Exception {
        String email = "test@gmail.com";
        String memberName = "tester";

        createTestMember(email, memberName);

        RegisterRequest data = RegisterRequest.builder()
                .email(email)
                .password("helloTester!@#")
                .memberName(memberName)
                .build();

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(status().is4xxClientError());
    }

    private void createTestMember(String email, String memberName) {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(email)
                .password("helloTester!@#")
                .memberName(memberName)
                .build();

        Member member = modelMapper.map(registerRequest, Member.class);

        memberRepository.save(member);
    }

}