package com.ming.mingcommerce.order.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class OrderLine {
    private String cartLineUuid;

    public OrderLine(String cartLineUuid) {
        this.cartLineUuid = cartLineUuid;
    }

    public static OrderLine create(String cartLineUuid) {
        return new OrderLine(cartLineUuid);
    }
}
