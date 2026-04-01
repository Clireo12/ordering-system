package com.example.mixue.service;

import com.example.mixue.pojo.Category;
import com.example.mixue.pojo.Product;
import com.example.mixue.pojo.dto.CategoryDto;
import com.example.mixue.pojo.dto.CategorySimpleDto;
import com.example.mixue.pojo.dto.ProductDto;

import java.util.List;

public interface IMenuService {

    /**
     * 获取完整菜单（包含分类和商品）
     * @return 分类DTO列表
     */
    List<CategoryDto> getFullMenu();

    /**
     * 获取全部分类
     */
    List<CategorySimpleDto> getAllCategoriesSimple();

    /**
     * 获取单个分类及其商品
     */
    CategoryDto getCategoryWithProducts(Integer categoryId);

    List<ProductDto> searchProducts(String keyword);
    ProductDto getProductDetail(Integer productId);

}