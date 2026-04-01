package com.example.mixue.pojo;

import jakarta.persistence.*;

@Table(name="tb_user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer userId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "phone")
    private String phone;

    @Column(columnDefinition = "integer default 0")
    private Integer coin = 0; // 雪王币

    @Column(columnDefinition = "integer default 0")
    private Integer coupons = 0; // 优惠券数量

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getCoin() {
        return coin;
    }

    public Integer getCoupons() {
        return coupons;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public void setCoupons(Integer coupons) {
        this.coupons = coupons;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", coin=" + coin +
                ", coupons=" + coupons +
                '}';
    }
}

