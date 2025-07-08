package com.projectfyp.repository;

import com.projectfyp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByIsActiveTrue();

    List<Product> findByCategory(String category);
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
    Page<Product> findByCategoryAndNameContainingIgnoreCaseAndIsActiveTrue(String category, String name, Pageable pageable);
    List<Product> findTop8ByIsActiveTrueOrderByIdDesc();
    List<Product> findTop4ByCategoryAndIsActiveTrueAndIdNotOrderByIdDesc(String category, Integer id);
}
