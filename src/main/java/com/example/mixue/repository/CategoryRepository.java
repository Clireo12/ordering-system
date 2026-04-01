package com.example.mixue.repository;

import com.example.mixue.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    /**
     * 按显示顺序升序查询所有分类
     */
    List<Category> findAllByOrderByDisplayOrderAsc();

}