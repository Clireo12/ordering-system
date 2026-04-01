package com.example.mixue.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 50)
    private String temperature;

    @Column(nullable = false, length = 50)
    private String sugar;


    public Integer getId() {
        return id;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public Product getProduct() {
        return product;
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

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public void setProduct(Product product) {
        this.product = product;
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