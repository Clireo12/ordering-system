package com.example.mixue.pojo.dto;

public class CategorySimpleDto {
    private Integer id;
    private String name;
    private Integer displayOrder;

    public CategorySimpleDto(Integer id, String name, Integer displayOrder) {
        this.id = id;
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
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
}
