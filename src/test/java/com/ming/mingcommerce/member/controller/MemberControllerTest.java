package com.ming.mingcommerce.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.member.model.LoginRequest;
import com.ming.mingcommerce.member.model.RegisterRequest;
import com.ming.mingcommerce.member.model.RegisterResponse;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
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
    @DisplayName("로그인 - 성공")
    void login() throws Exception {
        String email = "tester@gmail.com";
        String password = "tester123!@";

        createTestMember(email, password);

        LoginRequest loginRequest = new LoginRequest(email, password);
        String data = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(data))

                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists())
                .andDo(document("login", requestFields(
                                fieldWithPath("email").description("로그인을 진행할 이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("액세스토큰. 7일간 유효하다"),
                                fieldWithPath("refreshToken").description("리프레시토큰. 30일간 유효하다")
                        )));
    }

    @Test
    @DisplayName("로그인 - 비밀번호 불일치로 실패")
    void login_fail_by_wrong_password() throws Exception {
        String email = "tester@gmail.com";
        String password = "tester123!@";

        createTestMember(email, password);

        String wrongPassword = "tester123";
        LoginRequest loginRequest = new LoginRequest(email, wrongPassword);
        String data = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(data))

                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void registerFail_DuplicatedEmail() throws Exception {
        String email = "test@gmail.com";
        String password = "tester123!@";

        createTestMember(email, password);

        RegisterRequest data = RegisterRequest.builder()
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("jwt 토큰을 이용하여 멤버 정보를 반환한다")
    void memberInfo() throws Exception {
        RegisterResponse response = createTestMember("test@gmail.com", "tester369!");
        mockMvc.perform(get("/api/members/info").header("X-WWW-MING-AUTHORIZATION", response.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").exists())
                .andDo(document("get-member-info",
                        requestHeaders(
                                headerWithName("X-WWW-MING-AUTHORIZATION").description("jwt 토큰 형식의 밍커머스 인증헤더")
                        ),
                        responseFields(
                                fieldWithPath("result.email").description("멤버의 이메일"),
                                fieldWithPath("result.memberName").description("멤버의 이름")
                        )
                ))
        ;
    }


    private RegisterResponse createTestMember(String email, String password) {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(email)
                .password(password)
                .memberName("tester")
                .build();

        return memberService.register(registerRequest);
    }
}
