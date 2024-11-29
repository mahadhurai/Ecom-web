package com.maha.service;

import com.maha.model.OrderRequest;
import com.maha.model.ProductOrder;
import java.util.List;


public interface OrderService {
    
    public void saveOrder(Integer userId, OrderRequest orderRequest);
    
    public List<ProductOrder> getOrderByUser(Integer userId);
    
    public Boolean updateOrderSts(Integer id, String status);
    
    public List<ProductOrder> getAllOrders();

    public ProductOrder getOrdersByOrderId(String trim);
}
