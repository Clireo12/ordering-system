package com.example.mixue.pojo.dto;

public class CartItemDto {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImage;
    private Double price;
    private Integer quantity;
    private String temperature;
    private String sugar;


    public Integer getId() {
        return id;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getSugar() {
        return sugar;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }
}