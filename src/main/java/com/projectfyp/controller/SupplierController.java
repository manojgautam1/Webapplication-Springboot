package com.projectfyp.controller;

import com.projectfyp.constants.StatusOrder;
import com.projectfyp.model.*;
import com.projectfyp.repository.CategoryRepository;
import com.projectfyp.repository.OrderRepository;
import com.projectfyp.service.*;
import jakarta.servlet.http.HttpSession;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class SupplierController {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProduceService produceService;

    @GetMapping("/")
    public String SupplierHome(Model model)
    {
        model.addAttribute("totalProducts", 15);
        model.addAttribute("totalOrders", 45);
        model.addAttribute("totalUsers", 12);

        // Sample data for chart
        model.addAttribute("orderLabels", List.of("Jan", "Feb", "Mar", "Apr"));
        model.addAttribute("orderData", List.of(10, 20, 15, 30));
        List<CustomerProduce> allProduces = produceService.getAllProduce();
        model.addAttribute("produces", allProduces);

        return "supplier/dashboard";
    }

    @GetMapping("/addproduct")
    public String Supplieraddprodcut(Model m)
    {
        List<ProductCategory> categories= categoryService.findAll();
        m.addAttribute("categories", categories);
        return "supplier/add_product";
    }


    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m)
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
    @GetMapping("/category")
    public String category(Model m){
        m.addAttribute("categories", categoryService.findAll());

        return "supplier/category";

    }
    @PostMapping("/savecategory")
    public String saveCategory(@ModelAttribute ProductCategory category,HttpSession session){
        boolean existCategory= categoryService.existCategory(category.getCategoryname());
        if(existCategory){
            session.setAttribute("errorMsg", "Category already exist");
        }
        else
        {
            ProductCategory saveCategory =categoryService.saveCategory(category);
            if(ObjectUtils.isEmpty(saveCategory))
            {
                session.setAttribute("errorMsg", "Category not saved");
            }else
            {
                session.setAttribute("succMsg", "Category saved");
            }
        }

       return "redirect:/admin/category";
    }
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session){
        Boolean deleteCategory= categoryService.deleteCategory(id);
        if(deleteCategory)
        {
            session.setAttribute("succMsg", "Category deleted successfully");
        }
        else{
            session.setAttribute("errorMsg", "Category not deleted");
        }
        return "redirect:/admin/category";
    }
    @GetMapping("/editcategory/{id}")
    public String editCategory(@PathVariable int id,Model m, HttpSession session)
    {
        m.addAttribute("category",categoryService.getCategoryById(id));
        return "/supplier/editcategory";
    }
    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute ProductCategory category,HttpSession session)
    {
        ProductCategory oldCategory= categoryService.getCategoryById(category.getId());
        if(!ObjectUtils.isEmpty(oldCategory))
        {
            oldCategory.setCategoryname(category.getCategoryname());
            oldCategory.setCategorydescription(category.getCategorydescription());
            oldCategory.setIsAvailable(category.getIsAvailable());
            session.setAttribute("errorMsg", "Category updated");
        }
        ProductCategory updateCategory= categoryService.saveCategory(oldCategory);
        if(!ObjectUtils.isEmpty(updateCategory))
        {
            session.setAttribute("succMsg", "Category updated successfully");
        }else {
            session.setAttribute("errorMsg", "Category not updated");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/viewproducts")
    public String viewProducts(@RequestParam(value = "category", required = false) String category, Model model) {
        List<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getallProduct();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findByIsAvailableTrue());
        return "supplier/product";
    }

    @GetMapping("/editproducts/{id}")
    public String editproducts(@PathVariable int id, Model m)
    {
        m.addAttribute("product", productService.getProductById(id));
        return "/supplier/edit_product";
    }

    @GetMapping("/deleteproduct/{id}")
    public String deleteproducts(@PathVariable int id,HttpSession session)
    {
         Boolean delete = productService.deleteProduct(id);
         if(delete)
         {
             session.setAttribute("succMsg","product deleted successfully");
         }else {
             session.setAttribute("errorMsg","product not deleted successfully");
         }

        return "redirect:/admin/viewproducts";
    }
    @PostMapping("/updateProduct")
    public String updateproducts(@ModelAttribute Product product, HttpSession session,@RequestParam("file") MultipartFile image,Model m)
    {
        if(product.getDiscount() < 0 || product.getDiscount() > 100)
        {
            session.setAttribute("errorMsg", "Discount is invalid");
        }else {
            Product updateProduct = productService.updateProduct(product,image);
            if(!ObjectUtils.isEmpty(updateProduct))
            {
                session.setAttribute("succMsg","product updated successfully");
            }else {
                session.setAttribute("errorMsg","product update Failed");
            }
        }
        //return "redirect:/admin/editproducts/"+product.getId();
        return "redirect:/admin/viewproducts";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) throws IOException {
        if(product.getDiscount() < 0 || product.getDiscount() > 100)
        {
            session.setAttribute("errorMsg", "Discount is invalid");
        }else {
            String imageName=image.isEmpty()?"default.jpeg": image.getOriginalFilename();

            product.setImage(imageName);
            Double discount =product.getPrice()*(product.getDiscount()/100.0);
            Double discountprice = product.getPrice()-discount;
            product.setDiscountPrice(discountprice);
            Product saveProduct = productService.saveProduct(product);
                if (!ObjectUtils.isEmpty(saveProduct)) {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator + image.getOriginalFilename());
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    session.setAttribute("succMsg", "Product has been saved");
                } else {
                    session.setAttribute("errorMsg", "Something went wrong while saving product");
                }
        }
        return "redirect:/admin/addproduct";
    }

    @GetMapping("/user-detail")
    public String getAllUsers(Model m)
    {
        List<Users> users= userService.getAllUsers("ROLE_USER");
        m.addAttribute("users", users);
        return "/supplier/userdetails";
    }


    @GetMapping("/orderdetails")
    public String getAllOrders(Model m)
    {
        List<ProductOrder> allOrders= orderService.getAllOrders();
        m.addAttribute("orders", allOrders);
        return "supplier/orderdetails";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer o_status, HttpSession session)
    {
        StatusOrder[] values = StatusOrder.values();
        String status=null;
        for(StatusOrder orderst : values)
        {
            if(orderst.getId().equals(o_status))
            {
                status=orderst.getName();
            }
        }
        Boolean updateOrder= orderService.updateOrderStatus(id, status);
        if(updateOrder)
        {
            session.setAttribute("succMsg", "Order has been cancelled");
        }
        else
        {
            session.setAttribute("errorMsg", "Something went wrong while cancelling the order");
        }
        return "redirect:/admin/orderdetails";
    }
    @GetMapping("/filtered")
    public String viewOrders(@RequestParam(value = "keyword", required = false) String keyword, Model m) {
        List<ProductOrder> orders;
        if (keyword != null && !keyword.isEmpty()) {
            orders = orderService.findOrdersByProductName(keyword);
        } else {
            orders = orderService.getAllOrders();
        }

        m.addAttribute("orders", orders);
        m.addAttribute("keyword", keyword); // for retaining input
        return "supplier/orderdetails";
    }

}

