package com.example.mixue.service;

import com.example.mixue.pojo.User;
import com.example.mixue.pojo.dto.UserDto;

import java.util.Optional;

public interface IUserService {

    /*
    *   获取所有用户信息
    * */
    Iterable<User> getAllUsers();

    /*
     *  增加用户
     * */
    User add(User user);

    /*
    *   查询用户
    * */
    User getUser(Integer userId);

    /*
     *   修改用户
     * */
    User edit(UserDto user);


    /*
     *   删除用户
     * */
    void delete(Integer userId);

    /*
    *   登录验证用户
    * */
    Optional<User> findByPhone(String phone);


}
