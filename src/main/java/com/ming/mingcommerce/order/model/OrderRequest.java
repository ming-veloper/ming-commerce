package com.ming.mingcommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
