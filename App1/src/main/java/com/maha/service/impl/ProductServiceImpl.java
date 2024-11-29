package com.maha.service.impl;

import com.maha.model.Product;
import com.maha.repository.ProductRepo;
import com.maha.service.ProductService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    public ProductRepo proRepo;

    @Override
    public Product saveProduct(Product product) {
        return proRepo.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productsList = new ArrayList<>();
        proRepo.findAll().forEach(productsList::add);
        return productsList;
    }

    @Override
    public Boolean dltProduct(Integer id) {
        Product product = proRepo.findById(id).orElse(null);
        if (product != null) {
            proRepo.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(Integer id) {
        Product product = proRepo.findById(id).orElse(null);
        return product;
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {
        Product dbProduct = getProductById(product.getId());
        String imgName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();
        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setImage(imgName);
        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setDiscount(product.getDiscount());
        //100rs->5%discount
        //100*5/100=5
        //100-5=95
        Double discount = product.getPrice() * (product.getDiscount() / 100.0);
        Double discountPrice = product.getPrice() - discount;
        dbProduct.setDiscountPrice(discountPrice);

        Product update = proRepo.save(dbProduct);
        if (update != null) {
            if (!image.isEmpty()) {
                try {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
            return product;
        }
        return null;
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> product = null;
        if (category != null && !category.isEmpty()) {
            // Log category to verify it's being received correctly
            System.out.println("Filtering by category: " + category);
            product = proRepo.findByCategoryIgnoreCaseAndIsActiveTrue(category);  // Adjusting for case insensitivity and active check
        } else {
            product = proRepo.findByIsActiveTrue();
        }
        return product;
    }

}
