package com.example.mixue.service;

import com.example.mixue.exception.BusinessException;
import com.example.mixue.pojo.*;
import com.example.mixue.pojo.dto.CartItemDto;
import com.example.mixue.pojo.dto.ShoppingCartDto;
import com.example.mixue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService implements IShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCartByUserId(Integer userId) {
        ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(404, "购物车不存在"));

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addItemToCart(Integer userId, Integer productId, String temperature, String sugar, Integer quantity) {
        // 获取或创建购物车
        ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(404, "用户不存在"));
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartRepository.save(newCart);
                });

        //初始化items集合
        if(cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        // 检查商品是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(404, "商品不存在"));

        // 检查是否已存在相同商品
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId)
                        && item.getTemperature().equals(temperature)
                        && item.getSugar().equals(sugar))
                .findFirst();

        if (existingItem.isPresent()) {
            // 更新现有商品数量
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // 添加新商品
            CartItem newItem = new CartItem();
            newItem.setShoppingCart(cart);  // 设置双向关联
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setTemperature(temperature);
            newItem.setSugar(sugar);

            cart.getItems().add(newItem);  // 添加到集合
        }
        //强制刷新（确保数据同步）
        shoppingCartRepository.saveAndFlush(cart);  // 使用 saveAndFlush 替代 save

        // 重新从数据库加载最新数据（确保返回最新状态）
        ShoppingCart refreshedCart = shoppingCartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(404, "购物车不存在"));

        return convertToDto(refreshedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto updateCartItem(Integer userId, Integer itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(404, "购物车项不存在"));

        // 验证该商品是否属于该用户的购物车
        if (!item.getShoppingCart().getUser().getUserId().equals(userId)) {
            throw new BusinessException(403, "无权修改此购物车项");
        }

        if (quantity != null) {
            if (quantity <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        }

        return convertToDto(shoppingCartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(404, "购物车不存在")));
    }

    @Override
    @Transactional
    public ShoppingCartDto removeItemFromCart(Integer userId, Integer itemId) {
        // 获取购物车（连带items）
        ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(404, "购物车不存在"));

        // 查找并移除指定item
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(404, "购物车项不存在"));

        // 从集合中移除（orphanRemoval会自动处理数据库删除）
        cart.getItems().remove(itemToRemove);

        // 保存购物车（触发级联操作）
        shoppingCartRepository.save(cart);

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto clearCart(Integer userId) {
        // 获取购物车并加载items集合
        ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(404, "购物车不存在"));

        // 创建临时集合避免并发修改异常
        List<CartItem> itemsToDelete = new ArrayList<>(cart.getItems());

        // 清空购物车集合
        cart.getItems().clear();

        // 显式删除所有关联项
        cartItemRepository.deleteAll(itemsToDelete);

        //强制刷新并重新加载
        shoppingCartRepository.saveAndFlush(cart);

        // 返回空的购物车DTO
        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setUserId(userId);
        dto.setItems(new ArrayList<>());
        dto.setTotalPrice(0.0);

        return dto;
    }

    private ShoppingCartDto convertToDto(ShoppingCart cart) {
        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setUserId(cart.getUser().getUserId());

        List<CartItemDto> itemDtos = cart.getItems().stream().map(item -> {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setProductImage(item.getProduct().getImage());
            itemDto.setPrice(item.getProduct().getPrice().doubleValue());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setTemperature(item.getTemperature());
            itemDto.setSugar(item.getSugar());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);

        //总价
        dto.setTotalPrice(0.0);

        return dto;
    }
}