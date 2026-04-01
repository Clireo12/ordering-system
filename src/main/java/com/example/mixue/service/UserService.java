package com.example.mixue.service;

import com.example.mixue.exception.BusinessException;
import com.example.mixue.pojo.ShoppingCart;
import com.example.mixue.pojo.User;
import com.example.mixue.pojo.dto.UserDto;
import com.example.mixue.repository.ShoppingCartRepository;
import com.example.mixue.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service    //spring的bean
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }


    //添加用户
    @Override
    public User add(User user) {
        try {
            User userPojo = new User();
            BeanUtils.copyProperties(user, userPojo);   // 属性拷贝
            User savedUser = userRepository.save(userPojo);

            // 创建购物车
            ShoppingCart cart = new ShoppingCart();
            cart.setUser(savedUser);
            shoppingCartRepository.save(cart);

            return savedUser;
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(400, "手机号已存在");
        } catch (Exception e) {
            throw new BusinessException(500, "用户注册失败");
        }
    }

    //根据ID查询用户
    @Override
    public User getUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户ID: " + userId + " 不存在"));
    }

    //修改用户信息
    @Override
    public User edit(UserDto user) {
        try {
            //  检查用户是否存在
            User existingUser = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new BusinessException(404, "用户ID: " + user.getUserId() + " 不存在"));

            // 检查手机号是否修改
            if (!existingUser.getPhone().equals(user.getPhone())) {
                //  检查新手机号是否已被其他用户使用
                Optional<User> userWithSamePhone = userRepository.findByPhone(user.getPhone());
                if (userWithSamePhone.isPresent() && !userWithSamePhone.get().getUserId().equals(user.getUserId())) {
                    throw new BusinessException(400, "手机号已被其他用户使用");
                }
            }

            // 更新用户信息
            existingUser.setUserName(user.getUserName());
            existingUser.setPassword(user.getPassword());
            existingUser.setPhone(user.getPhone());

            return userRepository.save(existingUser);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(500, "用户信息更新失败: " + e.getMessage());
        }
    }

    //删除用户
    @Override
    public void delete(Integer userId) {
        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            throw new BusinessException(500, "用户删除失败");
        }
    }

    //登录验证用户
    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

}
