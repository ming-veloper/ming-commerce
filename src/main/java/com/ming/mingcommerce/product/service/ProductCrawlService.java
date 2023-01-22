package com.ming.mingcommerce.product.service;

import com.ming.mingcommerce.product.ProductCrawler;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductCrawlService {

    private final ProductRepository productRepository;
    private final ProductCrawler productCrawler;

    public Map<String, String> crawl() throws IOException {
        List<Product> products = productCrawler.getProducts();
        productRepository.saveAll(products);

        return Map.of("message", "product successfully inserted");
    }


    private String parseDescription(String detailLink) {
        detailLink = "https://www.amazon.com" + detailLink;
        Connection conn = Jsoup.connect(detailLink)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
                .timeout(10000);
        Document document;
        try {
            document = conn.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Document parser = document.parser(Parser.htmlParser());

        Elements elements = parser.getElementsByClass("a-unordered-list").tagName("span");
        List<String> description = elements.stream().map(Element::html).toList();

        return String.join(",", description);
    }
}
