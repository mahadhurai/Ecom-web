package com.maha.controller;

import com.maha.model.Cart;
import com.maha.model.Category;
import com.maha.model.OrderRequest;
import com.maha.model.ProductOrder;
import com.maha.model.UserDtls;
import com.maha.repository.UserRepo;
import com.maha.service.CartService;
import com.maha.service.CategoryService;
import com.maha.service.OrderService;
import com.maha.service.UserService;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    CartService cartService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    CategoryService catService;

    @Autowired
    OrderService orderService;

    @ModelAttribute
    public void getUserDetails(HttpSession session, Model m) {
        UserDtls userDtls = (UserDtls) session.getAttribute("user");
        if (userDtls != null) {
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        } else {
            m.addAttribute("user", null);
            m.addAttribute("countCart", 0);
        }
        List<Category> allActiveCat = catService.getAllActiveCat();
        m.addAttribute("categorys", allActiveCat);
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String pwd,
            HttpSession session, @RequestParam(required = false) Integer redirectTo) {

        UserDtls user = userService.authenticateUser(email, pwd);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            session.setAttribute("Error", "Invalid email or password.");
            return "redirect:/user/login";
        }
    }

    @PostMapping("/forgot-password")
    public String changePassword(@RequestParam("cpwd") String email,
            @RequestParam("npwd") String newPassword,
            HttpSession session, Model model) {

        UserDtls user = userService.getUserByEmail(email);

        if (user == null) {
            session.setAttribute("Error", "User not found with this email");
            return "redirect:/user/forgot-password";
        }

        user.setPwd(newPassword);
        userService.saveUser(user);
        session.setAttribute("Success", "Password changed successfully");

        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "user/forgot_password";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        UserDtls user = userService.getUserById(uid);

        if (user == null) {
            session.setAttribute("Error", "User not found");
            return "redirect:/user/login";
        }

        Cart cart = cartService.saveCart(pid, uid);
        session.setAttribute("Success", "Product added to cart");

        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model m) {
        UserDtls userDtls = (UserDtls) session.getAttribute("user");

        if (userDtls == null) {
            return "redirect:/user/login";
        }

        List<Cart> carts = cartService.getCartByUser(userDtls.getId());
        m.addAttribute("carts", carts);
        m.addAttribute("user", userDtls);
        Integer countCart = cartService.getCountCart(userDtls.getId());
        m.addAttribute("countCart", countCart);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateCartQuantity(sy, cid);
        return "redirect:/user/cart";
    }

    @GetMapping("/orders")
    public String order(Model m, HttpSession session) {
        UserDtls userDtls = (UserDtls) session.getAttribute("user");
        if (userDtls == null) {
            return "redirect:/user/login";
        }
        List<Cart> carts = cartService.getCartByUser(userDtls.getId());
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
            m.addAttribute("orderPrice", orderPrice);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest req, HttpSession session) {
//        System.out.println(req);
        UserDtls user = (UserDtls) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        orderService.saveOrder(user.getId(), req);
        return "user/success";
    }

    @GetMapping("/success")
    public String success() {
        return "user/success";
    }

    @GetMapping("/my-orders")
    public String myOrder(Model m, HttpSession session) {
        UserDtls userDtls = (UserDtls) session.getAttribute("user");
        if (userDtls == null) {
            return "redirect:/user/login";
        }
        List<ProductOrder> orders = orderService.getOrderByUser(userDtls.getId());
        m.addAttribute("orders", orders);
        return "user/my_order";
    }

    @GetMapping("/update_status")
    public String updateOrdersSts(@RequestParam Integer id, @RequestParam String st, HttpSession session) {
        Boolean update = orderService.updateOrderSts(id, st);
        if (update) {
            session.setAttribute("Sucess", "Status Updated");
        } else {
            session.setAttribute("Error", "Status not uptate");
        }
        return "redirect:/user/user-orders";
    }

    @GetMapping("/profile")
    public String profile() {
        return "user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls userDtls, @RequestParam("img") MultipartFile img,
            HttpSession session) {

        UserDtls currentUser = (UserDtls) session.getAttribute("user");
        if (currentUser == null || currentUser.getId() == null) {
            session.setAttribute("Error", "User is not logged in or session expired.");
            return "redirect:/user/login";
        }
        userDtls.setId(currentUser.getId());
        try {
            userService.updateUserProfile(userDtls, img);
            session.setAttribute("Success", "Profile updated successfully!");
        } catch (Exception e) {
            session.setAttribute("Error", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,
            HttpSession session) {

        UserDtls userDtls = (UserDtls) session.getAttribute("user");
        if (userDtls == null) {
            return "redirect:/user/login";
        }
        if (currentPassword.equals(userDtls.getPwd())) {
            userDtls.setPwd(newPassword);
            UserDtls updateUser = userService.updateUser(userDtls);

            if (ObjectUtils.isEmpty(updateUser)) {
                session.setAttribute("Error", "Password not updated !! Error in server");
            } else {
                session.setAttribute("Success", "Password Updated successfully");
            }
        } else {
            session.setAttribute("Error", "Current Password incorrect");
        }

        return "redirect:/user/profile";

    }

    @GetMapping("/dltAcc")
    public String deleteAccount(HttpSession session) {
        UserDtls userDtls = (UserDtls) session.getAttribute("user");

        if (userDtls == null) {
            return "redirect:/user/login";
        }
        try {
            Integer userId = userDtls.getId();
            Optional<UserDtls> userOpt = userRepo.findById(userId);
            if (userOpt.isPresent()) {
                userRepo.delete(userOpt.get());
                session.setAttribute("Success", "User Deleted Successfully");
                return "redirect:/user/logout";
            } else {
                session.setAttribute("error", "User not found.");
                return "index";
            }
        } catch (ConstraintViolationException e) {
            session.setAttribute("error", "Error deleting user account: " + e.getMessage());
        } 
        return null;
    }
}

    

