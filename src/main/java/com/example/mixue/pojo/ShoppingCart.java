package com.example.mixue.pojo;

import jakarta.persistence.*;
import java.util.List;

/**
 * 购物车实体类
 * 映射数据库中的购物车表
 */
@Entity // 标识为JPA实体
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增策略
    private Integer id; // 购物车ID

    @OneToOne // 一对一关系
    @JoinColumn(name = "user_id", nullable = false) // 外键列
    private User user; // 关联的用户

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true) // 一对多关系
    private List<CartItem> items; // 购物车中的商品项

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}