package com.example.mixue.service;

import com.example.mixue.exception.BusinessException;
import com.example.mixue.pojo.Category;
import com.example.mixue.pojo.Product;
import com.example.mixue.pojo.dto.CategoryDto;
import com.example.mixue.pojo.dto.CategorySimpleDto;
import com.example.mixue.pojo.dto.ProductDto;
import com.example.mixue.repository.CategoryRepository;
import com.example.mixue.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service // 标识为Spring服务组件
public class MenuService implements IMenuService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * 获取完整菜单
     * @return 分类列表(包含商品)
     */
    @Override
    public List<CategoryDto> getFullMenu() {
        // 查询所有分类并按显示顺序排序
        List<Category> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();

        // 转换为DTO并填充商品数据
        return categories.stream().map(category -> {
            CategoryDto categoryDto = new CategoryDto();
            // 复制基本属性
            categoryDto.setId(category.getId());
            categoryDto.setName(category.getName());
            categoryDto.setDisplayOrder(category.getDisplayOrder());

            // 查询该分类下的所有商品
            List<Product> products = productRepository.findByCategoryId(category.getId());
            List<ProductDto> productDtos = products.stream().map(product -> {
                ProductDto productDto = new ProductDto();
                // 复制商品属性
                productDto.setId(product.getId());
                productDto.setCategoryId(product.getCategory().getId());
                productDto.setImage(product.getImage());
                productDto.setName(product.getName());
                productDto.setDescription(product.getDescription());
                productDto.setPrice(product.getPrice());
                return productDto;
            }).collect(Collectors.toList());

            // 设置商品列表
            categoryDto.setProducts(productDtos);
            return categoryDto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取所有分类(简化版)
     * @return 分类简化列表
     */
    @Override
    public List<CategorySimpleDto> getAllCategoriesSimple() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategorySimpleDto(c.getId(), c.getName(), c.getDisplayOrder()))
                .collect(Collectors.toList());
    }

    /**
     * 获取分类及其商品
     * @param categoryId 分类ID
     * @return 分类数据传输对象(包含商品)
     */
    @Override
    public CategoryDto getCategoryWithProducts(Integer categoryId) {
        // 查询分类
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(404, "分类ID: " + categoryId + " 不存在"));

        // 查询该分类下的商品
        List<Product> products = productRepository.findByCategoryId(categoryId);

        // 转换为DTO
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDisplayOrder(category.getDisplayOrder());

        // 转换商品为DTO
        List<ProductDto> productDtos = products.stream().map(product -> {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setImage(product.getImage());
            productDto.setDescription(product.getDescription());
            productDto.setPrice(product.getPrice());
            return productDto;
        }).collect(Collectors.toList());

        categoryDto.setProducts(productDtos);
        return categoryDto;
    }

    /**
     * 搜索商品
     * @param keyword 关键词
     * @return 匹配的商品列表
     */
    @Override
    public List<ProductDto> searchProducts(String keyword) {
        // 确保keyword不为null
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchKey = keyword.trim();
        System.out.println("搜索关键词: " + searchKey);

        List<Product> products = productRepository.findByNameContainingIgnoreCase(searchKey);

        System.out.println("搜索结果数量: " + products.size());
        products.forEach(p -> System.out.println("匹配商品: " + p.getName()));

        // 转换DTO
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品数据传输对象
     */
    @Override
    public ProductDto getProductDetail(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(404, "商品不存在"));
        return convertToDto(product);
    }

    /**
     * 将Product转换为ProductDto
     * @param product 商品实体
     * @return 商品数据传输对象
     */
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setCategoryId(product.getCategory().getId());
        dto.setName(product.getName());
        dto.setImage(product.getImage());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        return dto;
    }
}
