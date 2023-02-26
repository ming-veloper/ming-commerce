package com.ming.mingcommerce.order.vo;

import com.ming.mingcommerce.cart.model.CartLineDTO;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class OrderLine {
    private String cartLineUuid;
    private String productId;
    private Double price;
    private Long quantity;
    private String productName;

    public OrderLine(CartLineDTO cartLine) {
        this.cartLineUuid = cartLine.getUuid();
        this.productId = cartLine.getProductId();
        this.price = cartLine.getPrice();
        this.quantity = cartLine.getQuantity();
        this.productName = cartLine.getProductName();
    }

    public static OrderLine create(CartLineDTO cartLine) {
        return new OrderLine(cartLine);
    }

    public Double calculatePrice() {
        return price * quantity;
    }
}
