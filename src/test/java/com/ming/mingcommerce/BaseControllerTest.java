package com.ming.mingcommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.util.JwtTokenUtil;
import org.junit.jupiter.api.Disabled;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class BaseControllerTest {
    public static final String X_WWW_MING_AUTHORIZATION = "X-WWW-MING-AUTHORIZATION";
    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public ModelMapper modelMapper;
    @Autowired
    public
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;

    public Member saveMember() {
        Member member = Member.builder().memberName("tester").role(Role.USER).email("tester@ming.com").password("tester123!").build();
        return memberRepository.saveAndFlush(member);
    }

    public List<Product> saveProduct() {
        Category category = new Category(CategoryName.DAIRY_EGGS);
        categoryRepository.save(category);

        for (int i = 1; i < 3; i++) {
            Product product = Product.builder().category(category).productName("신선 달걀 " + i + "구").price(7.89d).thumbnailImageUrl("http://helloworld.com/egg.png").description("신선 신선").build();

            productRepository.save(product);
        }

        return productRepository.findAll();
    }
}
