package com.maha.service;

import com.maha.model.Cart;
import java.util.List;

public interface CartService {
    
    public Cart saveCart(Integer productId, Integer userId);
    
    public List<Cart> getCartByUser(Integer userId);
    
    public Integer getCountCart(Integer userId);

    public void updateCartQuantity(String sy, Integer cid);
    
}
