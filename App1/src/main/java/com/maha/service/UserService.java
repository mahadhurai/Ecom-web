package com.maha.service;

import com.maha.model.UserDtls;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    
    public UserDtls saveUser(UserDtls user);

    public UserDtls getUserByEmail(String email);

    public UserDtls authenticateUser(String email, String pwd);

    public UserDtls getUserById(Integer uid);

    public UserDtls updateUserProfile(UserDtls user, MultipartFile img);

    public UserDtls updateUser(UserDtls userDtls);

    public String deleteAccount(UserDtls userDtls,Integer userId);

    public List<UserDtls> getAllUsers();
    
}
