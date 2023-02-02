package com.ming.mingcommerce.cart.controller;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.cart.model.CartProductDeleteRequest;
import com.ming.mingcommerce.cart.model.CartProductQuantityUpdate;
import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.member.service.MemberService;
import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.security.CurrentMember;
import com.ming.mingcommerce.util.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CartControllerTest extends BaseControllerTest {
    public static final String X_WWW_MING_AUTHORIZATION = "X-WWW-MING-AUTHORIZATION";
    @Autowired
    ProductRepository productRepository;

    @Autowired
    MemberService memberService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CartService cartService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("장바구니의 상품을 조회한다")
    void getCartProduct() throws Exception {
        // 멤버 생성과 상품 생성
        Member member = saveMember();
        saveProduct();
        // 상품 조회
        Product product = productRepository.findAll().stream().findFirst().get();
        // 장바구니에 상품 담기
        String productId = product.getProductId();
        CartProductRequest request = CartProductRequest.builder().productId(productId).quantity(10L).build();

        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        cartService.addProduct(currentMember, request);

        mockMvc.perform(get("/api/carts")
                        .header(X_WWW_MING_AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].productId").exists())
                .andExpect(jsonPath("$[0].price").exists())
                .andExpect(jsonPath("$[0].thumbnailImageUrl").exists())

                .andDo(document("get-cart-products",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("인증헤더")
                        ),
                        responseFields(
                                fieldWithPath("[].productId").description("상품 고유 id"),
                                fieldWithPath("[].thumbnailImageUrl").description("상품 썸네일 url"),
                                fieldWithPath("[].productName").description("상품의 이름"),
                                fieldWithPath("[].price").description("상품의 가격"),
                                fieldWithPath("[].quantity").description("상품의 수량"),
                                fieldWithPath("[].createdDate").description("생성일"),
                                fieldWithPath("[].modifiedDate").description("수정일")
                        )
                ))


        ;
    }

    @Test
    @DisplayName("장바구니에 상품을 추가한다")
    void addProduct() throws Exception {
        Member member = saveMember();
        saveProduct();

        Product product = productRepository.findAll().stream().findFirst().get();

        CartProductRequest request = CartProductRequest.builder().productId(product.getProductId()).quantity(10L).build();
        String data = objectMapper.writeValueAsString(request);
        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        mockMvc.perform(post("/api/carts").header(X_WWW_MING_AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON_VALUE).content(data))

                .andExpect(status().isOk()).andExpect(jsonPath("cartLineCount").exists()).andDo(document("add-product-to-cart", requestHeaders(headerWithName(X_WWW_MING_AUTHORIZATION).description("액세스 토큰")), requestFields(fieldWithPath("productId").description("상품 고유 id"), fieldWithPath("quantity").description("상품 수량")), responseFields(fieldWithPath("cartLineCount").description("장바구니에 담긴 상품의 수"))));
    }

    @Test
    @DisplayName("장바구니의 상품 수량을 수정한다")
    void updateQuantity() throws Exception {
        // 멤버 생성과 상품 생성
        Member member = saveMember();
        saveProduct();
        // 상품 조회
        Product product = productRepository.findAll().stream().findFirst().get();
        // 장바구니에 상품 담기
        String productId = product.getProductId();
        CartProductRequest request = CartProductRequest.builder().productId(productId).quantity(10L).build();

        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        cartService.addProduct(currentMember, request);

        // 장바구니에 상품 수량을 수정한다
        CartProductQuantityUpdate update = CartProductQuantityUpdate.builder().productId(productId).quantity(7L).build();
        String data = objectMapper.writeValueAsString(update);

        mockMvc.perform(put("/api/carts")
                        .header(X_WWW_MING_AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(data)).andExpect(status().isOk())
                .andExpect(jsonPath("cartLineCount").exists())

                .andDo(document("update-cart-product-quantity",
                        requestHeaders(headerWithName(X_WWW_MING_AUTHORIZATION).description("액세스 토큰")),
                        requestFields(fieldWithPath("productId").description("상품 고유 id"), fieldWithPath("quantity").description("업데이트할 상품 수량")),
                        responseFields(fieldWithPath("cartLineCount").description("장바구니에 담긴 상품의 수"))));
    }

    @Test
    @DisplayName("장바구니에 담긴 상품을 삭제한다")
    void deleteProduct() throws Exception {
        // 멤버 생성과 상품 생성
        Member member = saveMember();
        // 상품 3개 저장
        saveProduct();
        // 첫번째 상품 조회
        Product product = productRepository.findAll().stream().findFirst().get();
        // 장바구니에 상품 하나 담기
        String productId = product.getProductId();
        CartProductRequest request = CartProductRequest.builder().productId(productId).quantity(10L).build();
        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        cartService.addProduct(currentMember, request);

        // 장바구니의 상품 삭제. 이전에 하나의 상품을 담았으므로, 이 삭제 api 실행 후 상품이 0개가 되어야 한다.
        CartProductDeleteRequest deleteRequest = CartProductDeleteRequest.builder().productId(productId).build();
        String data = objectMapper.writeValueAsString(deleteRequest);
        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        mockMvc.perform(delete("/api/carts")
                        .header(X_WWW_MING_AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(data)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("cartLineCount").value(0))
                .andDo(print())
                .andDo(document("delete-cart-product",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("productId").description("삭제할 상품의 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("cartLineCount").description("장바구니에 담긴 상품의 개수")
                        )
                ))

        ;

    }

    private Member saveMember() {
        Member member = Member.builder().memberName("tester").role(Role.USER).email("tester@ming.com").password("tester123!").build();
        return memberRepository.saveAndFlush(member);
    }

    private void saveProduct() {
        Category category = new Category(CategoryName.DAIRY_EGGS);
        categoryRepository.save(category);

        for (int i = 1; i < 3; i++) {
            Product product = Product.builder().category(category).productName("신선 달걀 " + i + "구").price(7.89d).thumbnailImageUrl("http://helloworld.com/egg.png").description("신선 신선").build();

            productRepository.save(product);
        }
    }

}