package com.example.mixue.pojo.dto;

import java.util.List;

/**
 * 购物车数据传输对象
 */
public class ShoppingCartDto {
    private Integer userId; // 用户ID
    private List<CartItemDto> items; // 购物车项列表
    private Double totalPrice; // 总价格

    public Integer getUserId() {
        return userId;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

