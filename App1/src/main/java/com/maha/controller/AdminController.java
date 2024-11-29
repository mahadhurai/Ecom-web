package com.maha.controller;

import com.maha.model.Category;
import com.maha.model.Product;
import com.maha.model.ProductOrder;
import com.maha.model.UserDtls;
import com.maha.service.CartService;
import com.maha.service.CategoryService;
import com.maha.service.OrderService;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private CategoryService catService;

    @Autowired
    private ProductService proService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

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
    
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }
    
    @GetMapping("/profile")
    public String profile() {
        return "admin/admin";
    }

    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categories", catService.getAllCat());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCat(@ModelAttribute Category cat, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        cat.setImgName(imageName);

        Boolean existCategory = catService.existCat(cat.getName());

        if (existCategory) {
            session.setAttribute("Error", "Category Name already exists");
        } else {

            Category saveCategory = catService.saveCat(cat);

            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("Error", "Not saved ! internal server error");
            } else {

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
                        + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("Success", "Saved successfully");
            }
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String dltCat(@PathVariable int id, HttpSession session) {
        Boolean delete = catService.dltCat(id);
        if (delete) {
            session.setAttribute("Success", "Category delete success");
        } else {
            session.setAttribute("Error", "Something wrong on server");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable int id, Model m) {
        m.addAttribute("category", catService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category cat, @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {

        Category oldCategory = catService.getCategoryById(cat.getId());
        String imageName = file.isEmpty() ? oldCategory.getImgName() : file.getOriginalFilename();

        if (cat != null) {

            oldCategory.setName(cat.getName());
            oldCategory.setIsActive(cat.getIsActive());
            oldCategory.setImgName(imageName);
        }

        Category updateCategory = catService.saveCat(oldCategory);

        if (updateCategory != null) {

            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
                        + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("Success", "Category updated successfully");
        } else {
            session.setAttribute("Error", "Update failed! Server error.");
        }

        return "redirect:/admin/editCategory/" + cat.getId();
    }

    @GetMapping("/loadAddProduct")
    public String load(Model m) {
        List<Category> categories = catService.getAllCat();
        m.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @GetMapping("/products")
    public String viewProduct(Model m) {
        m.addAttribute("products", proService.getAllProducts());
        return "admin/products";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) throws IOException {

        String imgName = image != null && !image.isEmpty() ? image.getOriginalFilename() : "default.jpg";
        product.setImage(imgName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product save = proService.saveProduct(product);
        if (save != null) {
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("Success", "Product Saved Successfully ");

        } else {
            session.setAttribute("Error", "Not save ! server error");
        }

        return "redirect:/admin/loadAddProduct";

    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model m) {
        m.addAttribute("product", proService.getProductById(id));
        m.addAttribute("categories", catService.getAllCat());
        return "admin/edit_product";
    }

    @GetMapping("/deleteProduct/{id}")
    public String dltProduct(@PathVariable int id, HttpSession session) {
        Boolean dlt = proService.dltProduct(id);
        if (dlt) {
            session.setAttribute("Success", "Product deleted successs");
        } else {
            session.setAttribute("Error", "Something wrong on server");
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session, Model m) {

        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("Error", "Invalid Discount");
        } else {
            Product update = proService.updateProduct(product, image);
            if (update != null) {
                session.setAttribute("Success", "Product updated success");
            } else {
                session.setAttribute("Error", "Something wrong on server");
            }
        }
        return "redirect:/admin/editProduct/" + product.getId();
    }

    @GetMapping("/orders")
    public String getAllOrders(Model m) {
        List<ProductOrder> orders = orderService.getAllOrders();
        m.addAttribute("orders", orders);
        return "admin/orders";
    }

    @PostMapping("/login")
    public String login(@RequestParam("uname") String username, @RequestParam("pwd") String password, HttpSession session) {
        if ("maha".equals(username) && "maha@2003".equals(password)) {
            return "admin/index";
        } else {
            session.setAttribute("Error", "Invalid username or password");
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/users")
    public String getAllUsers(Model m, HttpSession session) {
        List<UserDtls> users = userService.getAllUsers();
        m.addAttribute("users", users);
        return "admin/users";
    }
}
