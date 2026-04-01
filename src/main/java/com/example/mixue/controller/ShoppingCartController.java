package com.example.mixue.controller;

import com.example.mixue.pojo.dto.ResponseMessage;
import com.example.mixue.pojo.dto.ShoppingCartDto;
import com.example.mixue.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 获取用户购物车
     * @param userId 用户ID
     * @return 购物车数据及操作结果
     */
    @GetMapping("/{userId}")    //url: localhost:8088/cart/{userId}      method:get
    public ResponseMessage<ShoppingCartDto> getCart(@PathVariable Integer userId) {
        ShoppingCartDto cart = shoppingCartService.getCartByUserId(userId);
        return ResponseMessage.success(cart);
    }


    /**
     * 向购物车添加商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @param temperature 温度要求
     * @param sugar 糖分要求
     * @param quantity 数量(默认为1)
     * @return 更新后的购物车数据
     */
    @PostMapping("/{userId}/add")    //url: localhost:8088/cart/{userId}/add      method:post
    public ResponseMessage<ShoppingCartDto> addItem(
            @PathVariable Integer userId,
            @RequestParam Integer productId,
            @RequestParam String temperature,
            @RequestParam String sugar,
            @RequestParam(defaultValue = "1") Integer quantity) {
        ShoppingCartDto cart = shoppingCartService.addItemToCart(userId, productId, temperature, sugar, quantity);
        return ResponseMessage.success(cart);
    }

    /**
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @param quantity 新数量(可选)
     * @return 更新后的购物车数据
     */
    @PutMapping("/{userId}/update/{itemId}")    //url: localhost:8088/cart/{userId}/update/{itemId}      method:put
    public ResponseMessage<ShoppingCartDto> updateItem(
            @PathVariable Integer userId,
            @PathVariable Integer itemId,
            @RequestParam(required = false) Integer quantity) {
        ShoppingCartDto cart = shoppingCartService.updateCartItem(userId, itemId, quantity);
        return ResponseMessage.success(cart);
    }

    /**
     * 从购物车移除商品
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @return 更新后的购物车数据
     */
    @DeleteMapping("/{userId}/remove/{itemId}")     //url: localhost:8088/cart/{userId}/remove/{itemId}     method:delete
    public ResponseMessage<ShoppingCartDto> removeItem(
            @PathVariable Integer userId,
            @PathVariable Integer itemId) {
        ShoppingCartDto cart = shoppingCartService.removeItemFromCart(userId, itemId);
        return ResponseMessage.success(cart);
    }

    /**
     * 清空购物车
     * @param userId 用户ID
     * @return 空购物车
     */
    @DeleteMapping("/{userId}/clear")   //url: localhost:8088/cart/{userId}/clear    method:delete
    public ResponseMessage<ShoppingCartDto> clearCart(@PathVariable Integer userId) {
        ShoppingCartDto cart = shoppingCartService.clearCart(userId);
        return ResponseMessage.success(cart);
    }
}