package com.example.mixue.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CategoryDto {
    private Integer id;
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "显示顺序不能为空")
    private Integer displayOrder;

    // 商品列表
    private List<ProductDto> products;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

}