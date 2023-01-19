package com.ming.mingcommerce.product;

import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductCrawler {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    

    public List<Product> getProducts() throws IOException {
        String url = "https://www.amazon.com/s?k=food&i=grocery&rh=n%3A16310101%2Cn%3A16310231&dc&ds=v1%3AK6elEP%2FxWS5G08aQFEAGWinE3FGmr5b9KnvuAMfes%2BI&crid=16I887VJBBHI1&qid=1673512801&rnid=16310101&sprefix=fo%2Caps%2C300&ref=sr_nr_n_3";
        Connection conn = Jsoup.connect(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
                .timeout(10000);
        Document document = conn.get();
        Document parser = document.parser(Parser.htmlParser());
        Elements elements = parser.select("div.s-card-border").attr("data-component-type", "s-search-result");
        List<ProductRequest> productRequestList = new ArrayList<>();

        // 카테고리 생성하고 저장한다
        Category category = new Category(CategoryName.DAIRY_EGGS);
        categoryRepository.save(category);

        elements.forEach(element -> {
            ProductRequest product = new ProductRequest();
            // 상품 이름 추출
            String productName = element.getElementsByClass("a-size-base-plus").html();
            product.setProductName(productName);

            // 상세 정보 링크 추출
            // TODO 상세 링크에서 상품 이미지들과 상품 상세설명 정보 저장
            String detailLink = element.getElementsByClass("s-underline-text").attr("href");
            // String description = parseDescription(detailLink);
            //product.setDescription(description);

            // 상품 썸네일 링크 추출
            String thumbnailLink = element.getElementsByClass("s-image").attr("src");
            product.setThumbnailImageUrl(thumbnailLink);

            // 상품 가격 추출. 가격이 없다면 "undefined" 으로 세팅 한다.
            String price = element.getElementsByClass("a-offscreen").html();
            if (price.length() == 0 || price.isBlank()) {
                product.setPrice("undefined");
            } else {
                product.setPrice(price);
            }

            // 상품 카테고리 설정
            product.setCategory(category);

            // 상품 상세 설정
            product.setDescription("awesome product!");


            productRequestList.add(product);
        });
        List<Product> products = productRequestList.stream()
                .map(p -> modelMapper.map(p, Product.class)).toList();
        return products;
    }
}
