package com.maha.service;

import com.maha.model.Product;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    
    public Product saveProduct(Product product);
    
    public List<Product> getAllProducts();
    
    public Boolean dltProduct(Integer id);
    
    public Product getProductById(Integer id);
    
    public Product updateProduct(Product product, MultipartFile image);
    
    public List<Product> getAllActiveProducts(String category);
}
