package com.ming.mingcommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.security.CurrentMember;
import org.junit.jupiter.api.Disabled;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class BaseServiceTest {
    @Autowired
    public MemberRepository memberRepository;
    @Autowired
    public CartService cartService;
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public ModelMapper modelMapper;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;

    public CurrentMember saveMember() {
        Member member = Member.builder().memberName("tester").role(Role.USER).email("tester@ming.com").password("tester123!").build();
        memberRepository.saveAndFlush(member);
        return modelMapper.map(member, CurrentMember.class);
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
