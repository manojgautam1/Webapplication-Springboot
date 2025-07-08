package com.projectfyp.repository;

import com.projectfyp.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByUserId(Integer userId);

    ProductOrder findByOrderId(String orderId);
//    List<ProductOrder> findByProductNameContainingIgnoreCase(String keyword);
}
