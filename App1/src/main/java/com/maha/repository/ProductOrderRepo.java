package com.maha.repository;

import com.maha.model.ProductOrder;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProductOrderRepo extends CrudRepository<ProductOrder, Integer>{

    @Query(value = "SELECT * FROM `order` WHERE user_id = :userId", nativeQuery = true)
    public List<ProductOrder> findUserById(@Param("userId") Integer userId);

    public ProductOrder findByOrderId(String orderId);
    
}
