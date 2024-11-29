package com.maha.repository;

import com.maha.model.UserDtls;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends CrudRepository<UserDtls, Long> {

    @Query(value = "SELECT * FROM user WHERE id = :id", nativeQuery = true)
    Optional<UserDtls> findById(@Param("id") Integer id); 

    public UserDtls findByEmail(String email);

    public void deleteById(Integer userId);
}
