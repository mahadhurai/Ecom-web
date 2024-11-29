package com.maha.service.impl;

import com.maha.model.Cart;
import com.maha.model.Product;
import com.maha.model.UserDtls;
import com.maha.repository.CartRepo;
import com.maha.repository.CategoryRepo;
import com.maha.repository.ProductRepo;
import com.maha.repository.UserRepo;
import com.maha.service.CartService;
import com.maha.service.ProductService;
import com.maha.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo catRepo;

    @Autowired
    private ProductRepo proRepo;

    @Autowired
    private ProductService proService;

    @Autowired
    private UserService userService;

    @Override
    public Cart saveCart(Integer pid, Integer uid) {
        Product product = proService.getProductById(pid);
        UserDtls user = userService.getUserById(uid);

        if (product == null || user == null) {
            return null;
        }
        Cart cartStatus = cartRepo.findByProductIdAndUserId(pid, uid);

        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        return cartRepo.save(cart);
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {
        List<Cart> carts = cartRepo.findByUserId(userId);
        Double totalOrderPrice = 0.0;
        List<Cart> updateCarts = new ArrayList<>();
        for (Cart c : carts) {
            Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
            c.setTotalPrice(totalPrice);
            totalOrderPrice = totalOrderPrice + totalPrice;
            c.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(c);
        }

        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer count = cartRepo.countByUserId(userId);
        return count == null ? 0 : count;
    }

    @Override
    public void updateCartQuantity(String sy, Integer cid) {
        Cart cart = cartRepo.findById(cid).get();
        int updateQuantity;
        if (sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;
            if (updateQuantity <= 0) {
                cartRepo.delete(cart);
            } else {
                cart.setQuantity(updateQuantity);
                cartRepo.save(cart);
            }
        } else {
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepo.save(cart);
        }

    }

}
