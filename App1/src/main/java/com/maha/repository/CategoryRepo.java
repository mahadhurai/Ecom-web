package com.maha.repository;

import com.maha.model.Category;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepo extends CrudRepository<Category, Integer>{
    
    public Boolean existsByName(String name);
    
     public List<Category> findByIsActiveTrue();
    
}
