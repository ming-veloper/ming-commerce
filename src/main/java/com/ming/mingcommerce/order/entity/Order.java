package com.ming.mingcommerce.order.entity;

import com.ming.mingcommerce.config.BaseTimeEntity;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.vo.OrderLine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

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
    // 주문 이름
    private String orderName;

    @ElementCollection
    @OrderColumn(name = "line_idx")
    @CollectionTable(name = "order_line", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderLine> orderLineList;

    private Double totalAmount; // 총 주문 금액

    public static Order create(Member member, List<OrderLine> orderLines) {
        Double totalAmount = calculateTotalAmount(orderLines);
        String orderName = extractOrderName(orderLines);
        return Order.builder()
                .member(member)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .orderName(orderName)
                .orderLineList(orderLines)
                .build();
    }

    public static Double calculateTotalAmount(List<OrderLine> orderLines) {
        return orderLines.stream()
                .mapToDouble(orderLine -> orderLine.calculatePrice() * 1000)
                .sum();
    }

    public static String extractOrderName(List<OrderLine> orderLines) {
        var firstProductName = orderLines.get(0).getProductName();
        String shortenFirstProductName = firstProductName.substring(0, firstProductName.length() / 2) + "...";
        return orderLines.size() > 1 ?
                shortenFirstProductName + " 외 " + orderLines.size() + "건" :
                shortenFirstProductName;
    }

    public void validateAmount(Double payRequestAmount) {
        if (!Objects.equals(payRequestAmount, this.totalAmount)) {
            throw new IllegalArgumentException(String.format("결제요청 금액과 주문 금액이 일치하지 않습니다. 결제요청금액 = %f, 주문금액 = %f", payRequestAmount, this.totalAmount));
        }
    }

    public void completePay() {
        this.orderStatus = OrderStatus.COMPLETE;
    }
}
