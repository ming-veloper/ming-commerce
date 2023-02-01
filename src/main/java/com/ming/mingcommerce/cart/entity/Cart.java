package com.ming.mingcommerce.cart.entity;

import com.ming.mingcommerce.cart.vo.CartLine;
import com.ming.mingcommerce.config.BaseTimeEntity;
import com.ming.mingcommerce.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartId;

    @ElementCollection
    @CollectionTable(name = "cart_line", joinColumns = @JoinColumn(name = "cart_id"))
    @OrderColumn(name = "cart_line_idx")
    private List<CartLine> productList = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    private Member member;

    public void setMember(Member member) {
        this.member = member;
    }
}
