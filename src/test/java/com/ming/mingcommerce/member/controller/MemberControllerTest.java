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
    @DisplayName("??????????????? ??????")
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
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("password").description("8~12?????? ????????? ???????????? ????????????"),
                                fieldWithPath("memberName").description("2~20??? ????????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("???????????????. 7?????? ????????????"),
                                fieldWithPath("refreshToken").description("??????????????????. 30?????? ????????????")
                        )
                ))

        ;
    }

    @Test
    @DisplayName("????????? ?????? ????????? ????????????")
    void checkEmailDuplication() throws Exception {
        mockMvc.perform(get("/api/members/email-duplication-check")
                        .param("email", "tester@gmail.com")
                )
                .andExpect(status().isOk())
                .andDo(document("check-email-duplication",
                        queryParameters(
                                parameterWithName("email").description("?????? ????????? ????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("isDuplicated").description("????????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? - ??????")
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
                                fieldWithPath("email").description("???????????? ????????? ?????????"),
                                fieldWithPath("password").description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("???????????????. 7?????? ????????????"),
                                fieldWithPath("refreshToken").description("??????????????????. 30?????? ????????????")
                        )));
    }

    @Test
    @DisplayName("????????? - ???????????? ???????????? ??????")
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
    @DisplayName("???????????? ?????? - ????????? ??????")
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
    @DisplayName("jwt ????????? ???????????? ?????? ????????? ????????????")
    void memberInfo() throws Exception {
        RegisterResponse response = createTestMember("test@gmail.com", "tester369!");
        mockMvc.perform(get("/api/members/info").header("X-WWW-MING-AUTHORIZATION", response.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").exists())
                .andDo(document("get-member-info",
                        requestHeaders(
                                headerWithName("X-WWW-MING-AUTHORIZATION").description("jwt ?????? ????????? ???????????? ????????????")
                        ),
                        responseFields(
                                fieldWithPath("result.email").description("????????? ?????????"),
                                fieldWithPath("result.memberName").description("????????? ??????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("????????? ????????? ?????? ????????? ?????? ????????? ????????????.")
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
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("????????????")),
                        requestFields(fieldWithPath("email").description("?????? ?????? ?????????"))
                ))
        ;
    }

    @Test
    @DisplayName("?????? ???????????? ??????????????? ???????????? ????????? ????????? ????????? ????????????.")
    void sendAuthenticationEmail_Fail() throws Exception {
        createTestMember("syhoneyjam@naver.com", "ming123@");
        MemberEmailAuthenticationRequest request = new MemberEmailAuthenticationRequest("syhoneyjam@naver.com");
        when(mailService.sendMail(anyString(), any())).thenReturn("success");
        mockMvc.perform(post("/api/members/change-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().is4xxClientError())
        ;
    }

    @Test
    @DisplayName("???????????? ???????????? ???????????? ????????????.")
    void changeEmail_Success() throws Exception {
        String currentEmail = "syhoneyjam@naver.com";
        String newEmail = "yeonnex@gmail.com";

        Member saveMember = saveMember(currentEmail);

        when(mailService.sendMail(anyString(), any())).thenReturn("success");
        memberService.sendEmail(newEmail, modelMapper.map(saveMember, CurrentMember.class));
        Member member = memberRepository.findMemberByEmail(currentEmail);

        mockMvc.perform(get("/api/members/change-email")
                        .queryParam("token", member.getEmailCheckToken())
                        .queryParam("email", newEmail)

                ).andExpect(status().isOk())
                .andDo(document("change-email",
                        queryParameters(
                                parameterWithName("token").description("????????? ?????? UUID ????????? ??????"),
                                parameterWithName("email").description("???????????? ???????????? ?????????")
                        )

                ));
    }

    @Test
    @DisplayName("????????? ????????? ????????? ??????????????? ????????? ????????? ????????????")
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
                .queryParam("email", newEmail)

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
        Member member = Member.builder().email(email).password("ming123@").role(Role.USER).memberName("?????????").build();
        return memberRepository.save(member);
    }
}
