package com.maha.service;

import com.maha.model.Category;
import java.util.List;


public interface CategoryService {
    
    public Category saveCat(Category cat);
    
    public Boolean existCat(String name);
    
    public List<Category> getAllCat();
    
    public Boolean dltCat(int id);
    
    public Category getCategoryById(int id);
    
    public List<Category> getAllActiveCat();
    
}
