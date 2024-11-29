package com.maha.repository;

import com.maha.model.Product;
import java.util.List;
import org.springframework.data.repository.CrudRepository;


public interface ProductRepo extends CrudRepository<Product, Integer>{

    public List<Product> findByIsActiveTrue();

    public List<Product> findByCategory(String category);
    
    public List<Product> findByCategoryIgnoreCaseAndIsActiveTrue(String category);
 
}
