package com.example.mixue.repository;

import com.example.mixue.pojo.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository  //spring的bean，标识为Spring仓库组件
public interface UserRepository extends CrudRepository<User,Integer> {

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户Optional对象
     */
    Optional<User> findByPhone(String phone);
}

