package com.projectfyp.repository;

import com.projectfyp.model.Cart;
import com.projectfyp.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    public List<CartItem> findByCartId(int cartId);
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.user.id = :userId")
    Integer sumQuantityByUserId(@Param("userId") Integer userId);
}
