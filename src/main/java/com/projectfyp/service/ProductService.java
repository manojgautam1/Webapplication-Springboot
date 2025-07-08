package com.projectfyp.service;

import com.projectfyp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);

    public List<Product> getallProduct();

    public Boolean deleteProduct(Integer id);

    public Product getProductById(Integer id);

    public Product updateProduct(Product product, MultipartFile file);

    public List<Product> getAllActiveProducts(String category);
    List<Product> getRecentlyAddedProducts();
    Page<Product> getPaginatedProducts(String category, String searchQuery, int page, int size);
    void reduceStock(Product product, int quantity);
    List<Product> getSimilarProducts(String category, Integer currentProductId);
    public List<Product> getProductsByCategory(String category);
}
