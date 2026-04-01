package com.example.mixue.service;

import com.example.mixue.pojo.dto.ShoppingCartDto;

/**
 * 购物车服务接口
 * 定义购物车相关操作
 */
public interface IShoppingCartService {

    /**
     * 获取用户购物车
     * @param userId 用户ID
     * @return 购物车数据传输对象
     */
    ShoppingCartDto getCartByUserId(Integer userId);

    /**
     * 向购物车添加商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @param temperature 温度要求
     * @param sugar 糖分要求
     * @param quantity 数量
     * @return 更新后的购物车数据传输对象
     */
    ShoppingCartDto addItemToCart(Integer userId, Integer productId, String temperature, String sugar, Integer quantity);

    /**
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @param quantity 新数量
     * @return 更新后的购物车数据传输对象
     */
    ShoppingCartDto updateCartItem(Integer userId, Integer itemId, Integer quantity);

    /**
     * 从购物车移除商品
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @return 更新后的购物车数据传输对象
     */
    ShoppingCartDto removeItemFromCart(Integer userId, Integer itemId);

    /**
     * 清空购物车
     * @param userId 用户ID
     * @return 空购物车数据传输对象
     */
    ShoppingCartDto clearCart(Integer userId);
}