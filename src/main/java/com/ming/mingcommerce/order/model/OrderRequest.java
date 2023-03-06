package com.ming.mingcommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private List<String> cartLineUuidList = new ArrayList<>();

    public void addCartLineUuid(List<String> uuidList) {
        cartLineUuidList.addAll(uuidList);
    }

    public String extractFirstCartLineUuid() {
        String s = cartLineUuidList.get(0);
        if (!StringUtils.hasText(s)) throw new IllegalArgumentException("카트 상품 UUID 가 존재하지 않습니다.");
        return s;
    }
}
