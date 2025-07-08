package com.projectfyp.service.impl;

import com.projectfyp.model.Product;
import com.projectfyp.repository.ProductRepository;
import com.projectfyp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getallProduct() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(Integer id) {
        Product product=productRepository.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(product)){
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image){
        Product dbproduct = getProductById(product.getId());
        String imageName=image.isEmpty()? dbproduct.getImage(): image.getOriginalFilename();
        dbproduct.setImage(imageName);
        dbproduct.setName(product.getName());
        dbproduct.setDescription(product.getDescription());
        dbproduct.setPrice(product.getPrice());
        dbproduct.setDiscount(product.getDiscount());
        dbproduct.setIsActive(product.getIsActive());

        Double discount =product.getPrice()*(product.getDiscount()/100.0);
        Double discountprice = product.getPrice()-discount;
        dbproduct.setDiscountPrice(discountprice);

        dbproduct.setStock(product.getStock());

        Product updatedProduct = productRepository.save(dbproduct);
        if(!ObjectUtils.isEmpty(updatedProduct))
        {
            try {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator + image.getOriginalFilename());
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }catch (Exception e)
                {
                    e.printStackTrace();
                }
            return product;
        }
        return null;
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products=null;
        if(ObjectUtils.isEmpty(category)){
            products= productRepository.findByIsActiveTrue();
        }else {
            products= productRepository.findByCategory(category);
        }

        return products;
    }
    @Override
    public List<Product> getRecentlyAddedProducts() {
        return productRepository.findTop8ByIsActiveTrueOrderByIdDesc();
    }
    @Override
    public Page<Product> getPaginatedProducts(String category, String searchQuery, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (!ObjectUtils.isEmpty(category)) {
            return productRepository.findByCategoryAndNameContainingIgnoreCaseAndIsActiveTrue(category, searchQuery, pageable);
        } else {
            return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchQuery, pageable);
        }
    }
    @Override
    public void reduceStock(Product product, int quantity) {
        if (product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        } else {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
    }
    @Override
    public List<Product> getSimilarProducts(String category, Integer currentProductId) {
        return productRepository.findTop4ByCategoryAndIsActiveTrueAndIdNotOrderByIdDesc(category, currentProductId);
    }
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
