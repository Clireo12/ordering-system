package com.example.mixue.repository;

import com.example.mixue.pojo.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    // 使用 @EntityGraph 解决懒加载问题
    @EntityGraph(attributePaths = "items")
    Optional<ShoppingCart> findByUser_UserId(Integer userId);

}