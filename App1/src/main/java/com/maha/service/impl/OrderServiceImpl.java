package com.maha.service.impl;

import com.maha.model.Cart;
import com.maha.model.OrderAddress;
import com.maha.model.OrderRequest;
import com.maha.model.ProductOrder;
import com.maha.repository.CartRepo;
import com.maha.repository.ProductOrderRepo;
import com.maha.service.OrderService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepo orderRepo;

    @Autowired
    private CartRepo cartRepo;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) {

        List<Cart> carts = cartRepo.findByUserId(userId);
        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());
            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());
            order.setStatus("In Progress");
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobile(orderRequest.getMobile());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            orderRepo.save(order);
        }
    }

    @Override
    public List<ProductOrder> getOrderByUser(Integer userId) {
        List<ProductOrder> order = orderRepo.findUserById(userId);
        return order;
    }

    @Override
    public Boolean updateOrderSts(Integer id, String status) {
        Optional<ProductOrder> findId = orderRepo.findById(id);
        if (findId.isPresent()) {
            ProductOrder proOrder = findId.get();
            proOrder.setStatus(status);
            orderRepo.save(proOrder);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return (List<ProductOrder>) orderRepo.findAll();
    }

    @Override
    public ProductOrder getOrdersByOrderId(String orderId) {
        return orderRepo.findByOrderId(orderId);
    }

}
