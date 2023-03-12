package com.ming.mingcommerce.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.mail.MailServiceImpl;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.model.*;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.member.service.MemberService;
import com.ming.mingcommerce.security.CurrentMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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

class MemberControllerTest extends BaseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MemberRepository memberRepository;

    @MockBean
    MailServiceImpl mailService;


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

    @Test
    @DisplayName("이메일 변경을 위한 이메일 인증 메일을 전송한다.")
    void sendAuthenticationEmail_Success() throws Exception {
        RegisterResponse member = createTestMember("syhoneyjam@naver.com", "ming123@");
        MemberEmailAuthenticationRequest request = new MemberEmailAuthenticationRequest("newemail@test.com");
        when(mailService.sendMail(anyString(), any())).thenReturn("success");
        mockMvc.perform(post("/api/members/send-email")
                        .header(X_WWW_MING_AUTHORIZATION, member.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andDo(document("send-authentication-mail",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("인증헤더")),
                        requestFields(fieldWithPath("email").description("변경 요청 이메일"))
                ))
        ;
    }

    @Test
    @DisplayName("현재 이메일과 변경하려는 이메일이 같다면 이메일 변경에 실패한다.")
    void sendAuthenticationEmail_Fail() throws Exception {
        RegisterResponse member = createTestMember("syhoneyjam@naver.com", "ming123@");
        MemberEmailAuthenticationRequest request = new MemberEmailAuthenticationRequest("syhoneyjam@naver.com");
        when(mailService.sendMail(anyString(), any())).thenReturn("success");
        mockMvc.perform(post("/api/members/change-email")
                .header(X_WWW_MING_AUTHORIZATION, member.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().is4xxClientError())
        ;
    }

    @Test
    @DisplayName("이메일이 인증되어 이메일을 변경한다.")
    void changeEmail_Success() throws Exception {
        String currentEmail = "syhoneyjam@naver.com";
        String newEmail = "yeonnex@gmail.com";

        Member saveMember = saveMember(currentEmail);
        JwtTokenModel tokenModel = jwtTokenUtil.issueToken(saveMember);

        when(mailService.sendMail(anyString(), any())).thenReturn("success");
        memberService.sendEmail(newEmail, modelMapper.map(saveMember, CurrentMember.class));
        Member member = memberRepository.findMemberByEmail(currentEmail);

        mockMvc.perform(get("/api/members/change-email")
                        .header(X_WWW_MING_AUTHORIZATION, tokenModel.getAccessToken())
                        .queryParam("token", member.getEmailCheckToken())
                        .queryParam("newEmail", newEmail)

                ).andExpect(status().isOk())
                .andDo(document("change-email",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("인증헤더")),
                        queryParameters(
                                parameterWithName("token").description("인증을 위한 UUID 형식의 토큰"),
                                parameterWithName("newEmail").description("변경되길 요청하는 이메일")
                        )

                ));
    }

    @Test
    @DisplayName("이메일 인증시 토큰이 불일치하여 이메일 변경에 실패한다")
    void changeEmail_Fail() throws Exception {
        String currentEmail = "syhoneyjam@naver.com";
        String newEmail = "yeonnex@gmail.com";

        Member saveMember = saveMember(currentEmail);
        JwtTokenModel tokenModel = jwtTokenUtil.issueToken(saveMember);

        when(mailService.sendMail(anyString(), any())).thenReturn("success");
        memberService.sendEmail(newEmail, modelMapper.map(saveMember, CurrentMember.class));

        mockMvc.perform(get("/api/members")
                .header(X_WWW_MING_AUTHORIZATION, tokenModel.getAccessToken())
                .queryParam("token", "this-is-wrong-token")
                .queryParam("newEmail", newEmail)

        ).andExpect(status().is4xxClientError());
    }


    private RegisterResponse createTestMember(String email, String password) {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(email)
                .password(password)
                .memberName("tester")
                .build();

        return memberService.register(registerRequest);
    }

    private Member saveMember(String email) {
        Member member = Member.builder().email(email).password("ming123@").role(Role.USER).memberName("밍밍이").build();
        return memberRepository.save(member);
    }
}
