package com.projectfyp.repository;

import com.projectfyp.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    public Integer countByUserId(Integer userId);
    public Cart findByUserId(Integer userId);
    public void deleteByUserId(Integer userId);
}
