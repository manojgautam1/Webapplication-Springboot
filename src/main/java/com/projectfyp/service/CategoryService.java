package com.projectfyp.service;



import com.projectfyp.model.ProductCategory;

import java.util.List;

public interface CategoryService {
    public ProductCategory saveCategory(ProductCategory Category);
    public List<ProductCategory> findAll();
    public boolean existCategory(String Categoryname);
    public Boolean deleteCategory(int id);
    public List<ProductCategory> getAllAvailableCategories();
    public ProductCategory getCategoryById(int id);
}
