package com.ming.mingcommerce.order.model;

/**
 * 사용자의 주문 목록을 조회하기 위한 Projection Model 입니다.
 * @author hope
 */
public interface MyOrderProjectionModel {
    String getOrderId();

    Double getTotalAmount();

    String getOrderName();

    String getThumbnailImageUrl();
}
