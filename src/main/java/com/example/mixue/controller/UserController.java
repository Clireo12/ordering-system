package com.example.mixue.controller;

import com.example.mixue.exception.BusinessException;
import com.example.mixue.pojo.User;
import com.example.mixue.pojo.dto.ResponseMessage;
import com.example.mixue.pojo.dto.UserDto;
import com.example.mixue.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*") // 允许跨域请求
@RestController  //接口方法返回对象，转换成json文本
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    /**
     * 添加用户
     * @param user 用户信息(经过验证)
     * @return 新增的用户信息
     */
    @PostMapping    //url: localhost:8088/user   method:post
    public ResponseMessage<User> add(@Validated @RequestBody User user){
        try {
            User userNew = userService.add(user);
            return ResponseMessage.success(userNew);
        } catch (Exception e) {
            throw new BusinessException(500, "增加用户失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")  //url: localhost:8088/user/{userId}  method:get
    public ResponseMessage get(@PathVariable Integer userId){
        try {
            User user = userService.getUser(userId);
            return ResponseMessage.success("成功获取用户信息"+user);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(404, "用户ID: " + userId + " 不存在");
        }
    }

    /**
     * 修改用户信息
     * @param user 用户更新信息(经过验证)
     * @return 更新后的用户信息
     */
    @PutMapping     //url: localhost:8088/user   method:put
    public ResponseMessage edit(@Validated @RequestBody UserDto user){
        try {
            User userNew = userService.edit(user);
            return ResponseMessage.success(userNew);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(500, "用户信息更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{userId}")  //url: localhost:8088/user/{userId}  method:delete
    public ResponseMessage delete(@PathVariable Integer userId){
        try {
            // 先检查用户是否存在
            userService.getUser(userId);
            userService.delete(userId);
            return ResponseMessage.success("成功删除用户ID: " + userId);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(404, "用户ID: " + userId + " 不存在，删除失败");
        } catch (Exception e) {
            throw new BusinessException(500, "删除用户ID: " + userId + " 失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    @GetMapping("/list")    //url: localhost:8088/user/list   method:get
    public ResponseMessage<List<User>> getAllUsers() {
        try {
            Iterable<User> users = userService.getAllUsers();
            List<User> userList = new ArrayList<>();
            users.forEach(userList::add);

            if (userList.isEmpty()) {
                return ResponseMessage.success("暂无用户数据" , userList);
            }
            return ResponseMessage.success("成功获取用户列表", userList);
        } catch (Exception e) {
            throw new BusinessException(500, "获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     * @param userDto 用户注册信息(经过验证)
     * @return 注册结果
     */
    @PostMapping("/register")   //url: localhost:8088/user/register   method:post
    public ResponseMessage<User> register(@Validated @RequestBody UserDto userDto) {
        try {
            // 检查手机号是否已存在
            if (userService.findByPhone(userDto.getPhone()).isPresent()) {
                return ResponseMessage.error(400, "手机号已注册");
            }

            User user = new User();
            user.setUserName(userDto.getUserName());
            user.setPassword(userDto.getPassword());
            user.setPhone(userDto.getPhone());


            User savedUser = userService.add(user);
            return ResponseMessage.success(savedUser);
        } catch (Exception e) {
            return ResponseMessage.error(500, "注册失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     * @param loginData 登录数据(包含手机号和密码)
     * @return 登录结果及用户信息
     */
    @PostMapping("/login")  //url: localhost:8088/user/login   method:post
    public ResponseMessage<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String phone = loginData.get("phone");

        try {
            User user = userService.findByPhone(phone)
                    .orElseThrow(() -> new BusinessException(404, "用户 " + phone + " 不存在"));

            if (!user.getPassword().equals(loginData.get("password"))) {
                return ResponseMessage.error(401, "密码错误");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("userName", user.getUserName());
            result.put("password",user.getPassword());
            result.put("phone", user.getPhone());
            result.put("coin", user.getCoin());
            result.put("coupons", user.getCoupons());

            return ResponseMessage.success(result);
        } catch (BusinessException e) {
            return ResponseMessage.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseMessage.error(500, "系统繁忙，请稍后再试");
        }
    }

}