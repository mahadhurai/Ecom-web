package com.maha.service.impl;

import com.maha.model.Category;
import com.maha.repository.CategoryRepo;
import com.maha.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo catRepo;

    @Override
    public Category saveCat(Category cat) {
        return catRepo.save(cat);
    }

    @Override
    public Boolean existCat(String name) {
        return catRepo.existsByName(name);
    }

    @Override
    public List<Category> getAllCat() {
        List<Category> categoriesList = new ArrayList<>();
        catRepo.findAll().forEach(categoriesList::add);
        return categoriesList;
    }

    @Override
    public Boolean dltCat(int id) {
        Category cat = catRepo.findById(id).orElse(null);
        if (cat != null) {
            catRepo.delete(cat);
            return true;
        }
        return false;
    }

    @Override
    public Category getCategoryById(int id) {
        Category cat = catRepo.findById(id).orElse(null);
        return cat;
    }

    @Override
    public List<Category> getAllActiveCat() {
        List<Category> categories = catRepo.findByIsActiveTrue();
        return categories;
    }

}
