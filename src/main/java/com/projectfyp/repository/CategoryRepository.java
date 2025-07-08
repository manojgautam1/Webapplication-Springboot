package com.projectfyp.repository;

import com.projectfyp.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<ProductCategory, Integer> {

    public Boolean existsByCategoryname(String Categoryname);

    public List<ProductCategory> findByIsAvailableTrue();
}
