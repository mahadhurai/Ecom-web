package com.maha.service.impl;

import com.maha.model.UserDtls;
import com.maha.repository.UserRepo;
import com.maha.service.UserService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDtls saveUser(UserDtls user) {
        UserDtls saveUser = userRepo.save(user);
        return saveUser;
    }

    public List<UserDtls> getAllUsers() {
        return (List<UserDtls>) userRepo.findAll(); 
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public UserDtls authenticateUser(String email, String pwd) {
        UserDtls user = userRepo.findByEmail(email);
        if (user != null && user.getPwd().equals(pwd)) {
            return user;
        }
        return null;
    }

    @Override
    public UserDtls getUserById(Integer uid) {
        return userRepo.findById(uid).orElse(null);
    }

    @Override
    public UserDtls updateUser(UserDtls user) {
        return userRepo.save(user);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls user, MultipartFile img) {
        Optional<UserDtls> optionalUser = userRepo.findById(user.getId());
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User not found with id: " + user.getId());
        }
        UserDtls dbUser = optionalUser.get();
        if (!img.isEmpty()) {
            dbUser.setImage(img.getOriginalFilename());
        }
        dbUser.setName(user.getName());
        dbUser.setMobile(user.getMobile());
        dbUser.setAddress(user.getAddress());
        dbUser.setCity(user.getCity());
        dbUser.setState(user.getState());
        dbUser.setPin(user.getPin());
        dbUser = userRepo.save(dbUser);

        try {
            if (!img.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + img.getOriginalFilename());
                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return dbUser;
    }

    @Override
    @Transactional
    public String deleteAccount(UserDtls userDtls,Integer userId) {
        try {
            if (userDtls == null) {
                System.out.println("User is null!");
                return "user/login";
            }
            System.out.println("Deleting user with ID: " + userDtls.getId());
            userRepo.deleteById(userId);
        } catch (Exception e) {
            System.out.println("Error while deleting user: " + e.getMessage());

        }
        return "/";
    }

}
