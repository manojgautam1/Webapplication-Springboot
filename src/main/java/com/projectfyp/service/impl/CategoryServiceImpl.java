package com.projectfyp.service.impl;

import com.projectfyp.model.ProductCategory;
import com.projectfyp.repository.CategoryRepository;
import com.projectfyp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductCategory saveCategory(ProductCategory category) {

        return categoryRepository.save(category);
    }

    @Override
    public List<ProductCategory> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public boolean existCategory(String categoryname) {
        return categoryRepository.existsByCategoryname(categoryname);
    }

    @Override
    public Boolean deleteCategory(int id) {
        ProductCategory category= categoryRepository.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(category)){
            categoryRepository.delete(category);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductCategory> getAllAvailableCategories() {
        List<ProductCategory> categories= categoryRepository.findByIsAvailableTrue();
        return categories;
    }

    @Override
    public ProductCategory getCategoryById(int id) {
        ProductCategory category= categoryRepository.findById(id).orElse(null);

        return category;
    }
}
