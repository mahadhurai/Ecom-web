package com.maha.controller;

import com.maha.model.Category;
import com.maha.model.Product;
import com.maha.model.UserDtls;
import com.maha.repository.ProductRepo;
import com.maha.service.CartService;
import com.maha.service.CategoryService;
import com.maha.service.ProductService;
import com.maha.service.UserService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {

    @Autowired
    private CategoryService catService;

    @Autowired
    private ProductService proService;

    @Autowired
    private ProductRepo proRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

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

    @GetMapping("/")
    public String index(Model m) {
        List<Category> allActiveCategory = catService.getAllActiveCat().stream()
                .sorted((c1, c2) -> c2.getId().compareTo(c1.getId())).limit(5).toList();
        List<Product> allActiveProducts = proService.getAllActiveProducts("").stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).limit(5).toList();
        m.addAttribute("category", allActiveCategory);
        m.addAttribute("products", allActiveProducts);
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
        List<Category> categories = catService.getAllActiveCat();

        List<Product> products;
        if (category.isEmpty()) {
            products = proRepo.findByIsActiveTrue();
        } else {
            products = proRepo.findByCategoryIgnoreCaseAndIsActiveTrue(category);
        }

        m.addAttribute("products", products);
        m.addAttribute("paramValue", category);
        m.addAttribute("categories", categories);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Product product = proService.getProductById(id);
        model.addAttribute("product", product);
        UserDtls user = (UserDtls) session.getAttribute("user");
        model.addAttribute("user", user);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
            throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setImage(imageName);

        UserDtls saveUser = userService.saveUser(user);

        if (saveUser != null) {
            if (!file.isEmpty()) {
                try {
                    File saveFile = new ClassPathResource("static/img/profile_img").getFile();
                    if (!saveFile.exists()) {
                        saveFile.mkdirs();
                    }
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    session.setAttribute("Error", "Error while saving image: " + e.getMessage());
                    return "redirect:/register";
                }
            }

            session.setAttribute("Success", "Registered successfully");
        } else {
            session.setAttribute("Error", "Something went wrong on the server");
        }

        return "redirect:/register";
    }

}
