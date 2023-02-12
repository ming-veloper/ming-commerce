package com.ming.mingcommerce.order.entity;

import com.ming.mingcommerce.config.BaseTimeEntity;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.vo.OrderLine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_order")
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;

    @ManyToOne
    private Member member;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING; // default
    @ElementCollection
    @OrderColumn(name = "line_idx")
    @CollectionTable(name = "order_line", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderLine> orderLineList;

    public void addOrderLine(OrderLine orderLine) {
        getOrderLineList().add(orderLine);
    }

    public Double calculateTotalAmount() {
        return orderLineList.stream()
                .mapToDouble(OrderLine::calculatePrice)
                .sum();
    }

    public String extractOrderName() {
        var firstProductName = getOrderLineList().get(0).getProductName();
        String shortenFirstProductName = firstProductName.substring(0, firstProductName.length() / 2);
        return shortenFirstProductName + "..." + " 외 " + getOrderLineList().size() + "건";
    }

    public static Order create(Member member) {
        return Order.builder()
                .member(member)
                .orderStatus(OrderStatus.PENDING)
                .orderLineList(new ArrayList<>())
                .build();
    }
}
