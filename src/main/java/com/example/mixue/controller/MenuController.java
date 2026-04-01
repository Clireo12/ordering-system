package com.example.mixue.controller;

import com.example.mixue.exception.BusinessException;
import com.example.mixue.pojo.Category;
import com.example.mixue.pojo.Product;
import com.example.mixue.pojo.dto.CategoryDto;
import com.example.mixue.pojo.dto.CategorySimpleDto;
import com.example.mixue.pojo.dto.ProductDto;
import com.example.mixue.pojo.dto.ResponseMessage;
import com.example.mixue.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "*") // 允许跨域请求
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    /**
     * 获取完整菜单
     * @return 包含所有分类和商品的菜单
     */
    @GetMapping     //url: localhost:8088/menu   method:get
    public ResponseMessage<List<CategoryDto>> getFullMenu() {
        try {
            List<CategoryDto> menu = menuService.getFullMenu();
            return ResponseMessage.success("成功获取菜单数据", menu);
        } catch (Exception e) {
            return ResponseMessage.error(500, "获取菜单失败: " + e.getMessage());
        }
    }

    /**
     * 获取全部分类
     */
    @GetMapping("/category")  //url: localhost:8088/menu/category   method:get
    public ResponseMessage<List<CategorySimpleDto>> getAllCategories() {
        List<CategorySimpleDto> list = menuService.getAllCategoriesSimple();
        return ResponseMessage.success("成功获取分类列表", list);
    }

    /**
     * 获取单个分类及其商品
     */
    @GetMapping("/category/{categoryId}")   //url: localhost:8088/menu/category/{categoryId}   method:get
    public ResponseMessage<CategoryDto> getCategoryWithProducts(@PathVariable Integer categoryId) {
        try {
            CategoryDto categoryDto = menuService.getCategoryWithProducts(categoryId);
            return ResponseMessage.success("成功获取分类及商品", categoryDto);
        } catch (BusinessException e) {
            return ResponseMessage.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseMessage.error(500, "获取分类详情失败: " + e.getMessage());
        }
    }


    @GetMapping("/search")      //url: localhost:8088/menu/search?keyword=***   method:get
    public ResponseMessage<List<ProductDto>> searchProducts(
            @RequestParam(required = false, defaultValue = "") String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseMessage.success("搜索成功", Collections.emptyList());
            }

            List<ProductDto> results = menuService.searchProducts(keyword);
            return ResponseMessage.success("搜索成功", results);
        } catch (Exception e) {
            return ResponseMessage.error(500, "搜索失败: " + e.getMessage());
        }
    }


    @GetMapping("/product/{productId}")      //url: localhost:8088/menu//product/{productId}  method:get
    public ResponseMessage<ProductDto> getProductDetail(@PathVariable Integer productId) {
        try {
            ProductDto product = menuService.getProductDetail(productId);
            return ResponseMessage.success("获取商品详情成功", product);
        } catch (BusinessException e) {
            return ResponseMessage.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseMessage.error(500, "获取商品详情失败: " + e.getMessage());
        }
    }
}
