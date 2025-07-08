package com.projectfyp.controller;

import com.projectfyp.model.CustomerProduce;
import com.projectfyp.model.Product;
import com.projectfyp.model.ProductCategory;
import com.projectfyp.model.Users;
import com.projectfyp.service.CartService;
import com.projectfyp.service.CategoryService;
import com.projectfyp.service.ProductService;
import com.projectfyp.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void getUserDetails(Principal p,Model m)
    {
        if(p!=null)
        {
            String email = p.getName();
            Users user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
            Integer countcart = cartService.getCountCart(user.getId());
            m.addAttribute("countCart", countcart);
        }
    }

    @GetMapping("/")
    public String index()
    {
        return "home";
    }
    @GetMapping("/home")
    public String home(Model m)
    {
        List<Product> recentProducts =productService.getRecentlyAddedProducts();
        m.addAttribute("recentProducts", recentProducts);
        return "home";
    }
    @GetMapping("/register")
    public String register(Model m, HttpSession session, RedirectAttributes redirectAttributes) {

        String verifiedMobile = (String) session.getAttribute("verifiedMobile");
        if (verifiedMobile == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Please verify your phone number first.");
            return "redirect:/phone-verification";
        }

        Users user = new Users();
        user.setMobileNumber(verifiedMobile);
        m.addAttribute("users", user);
        return "Register";
    }
    @GetMapping("/signin")
    public String Login()
    {
        return "Login";
    }
    @GetMapping("/phone-verification")
    public String phoneVerify()
    {
        return "phoneVerify";
    }
    @PostMapping("/otp-success")
    @ResponseBody
    public ResponseEntity<?> otpSuccess(@RequestBody Map<String, String> payload, HttpSession session) {
        String mobile = payload.get("mobile");

        if (mobile != null && mobile.matches("9\\d{9}")) {
            session.setAttribute("verifiedMobile", mobile);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Invalid mobile number");
    }

    @GetMapping("/blog")
    public String blog(){return "blog";}

    @GetMapping("/sellproduce")
    public String sellproduce(Model model)
    {
        model.addAttribute("produce", new CustomerProduce());
        return "sellproduce";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value="category",defaultValue = "") String category,
                           @RequestParam(value = "searchQuery", defaultValue = "") String searchQuery,
                           @RequestParam(value = "page", defaultValue = "0") int page)
    {
        int pageSize=12;

        Page<Product> products = productService.getPaginatedProducts(category, searchQuery, page, pageSize);
        List<ProductCategory> categories= categoryService.getAllAvailableCategories();
//        List<Product> products= productService.getAllActiveProducts(category);
        m.addAttribute("categories", categories);
        m.addAttribute("products", products.getContent());
        m.addAttribute("paramValue", category);
        m.addAttribute("searchQuery", searchQuery);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", products.getTotalPages());

        return "product";
    }
    @GetMapping("/proddetail/{id}")
    public String proddetail(@PathVariable int id, Model m)
    {
        Product product = productService.getProductById(id);
        m.addAttribute("products", product);
        List<Product> similarProducts = productService.getSimilarProducts(product.getCategory(), id);
        m.addAttribute("similarProducts", similarProducts);
        //m.addAttribute("products", productService.getProductById(id));
        return "productdetails";
    }
    @PostMapping("/saveUser")
    public String SaveUser(@Valid @ModelAttribute("users") Users user, BindingResult result, @RequestParam("PP") MultipartFile image, HttpSession session, Model model, RedirectAttributes redirectAttributes) throws IOException {
        String verifiedMobile = (String) session.getAttribute("verifiedMobile");

        if (verifiedMobile == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Session expired. Please verify your phone number again.");
            return "redirect:/phone-verification";
        }
        user.setMobileNumber(verifiedMobile);

        if(result.hasErrors())
        {
            model.addAttribute("users", user);
            return "Register";
        }

        if(userService.getUserByEmail(user.getEmail())!=null)
        {
            model.addAttribute("users", user);
            model.addAttribute("errorMsg", "Email already registered");
            return "Register";
        }
        Users existingByMobile = userService.getAllUsers("ROLE_USER").stream()
                .filter(u -> u.getMobileNumber().equals(user.getMobileNumber()))
                .findFirst().orElse(null);
        if(existingByMobile!=null){
            model.addAttribute("users", user);
            model.addAttribute("errorMsg", "Mobile number already in used");
            return "Register";
        }

        String imageName=image.isEmpty()?"default.jpeg": image.getOriginalFilename();
        System.out.print(imageName);
        user.setProfileImage(imageName);
        Users saveUser = userService.saveUser(user);
        if (!ObjectUtils.isEmpty(saveUser)) {
            if(!image.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile" + File.separator + image.getOriginalFilename());
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.removeAttribute("verifiedMobile");
            redirectAttributes.addFlashAttribute("succMsg", "User added successfully");
        } else {
            model.addAttribute("errorMsg", "Something went wrong while saving user");
            return "Register";
        }

        return "redirect:/signin";
    }

}