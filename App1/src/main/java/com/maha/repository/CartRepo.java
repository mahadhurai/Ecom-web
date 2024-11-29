package com.maha.repository;

import com.maha.model.Cart;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CartRepo extends CrudRepository<Cart, Integer>{
    
    public Cart findByProductIdAndUserId(Integer productId, Integer userId);

    public Integer countByUserId(Integer userId);

    public List<Cart> findByUserId(Integer userId);
}
