package com.projectfyp.controller;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectfyp.constants.StatusOrder;
import com.projectfyp.model.*;
import com.projectfyp.service.CartService;
import com.projectfyp.service.OrderService;
import com.projectfyp.service.ProduceService;
import com.projectfyp.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpSession httpSession;

    @Autowired
    private ProduceService produceService;

    // RestTemplate to make HTTP requests to the Flask API
    private final RestTemplate restTemplate = new RestTemplate();

    // Flask API endpoint URL
    private final String FLASK_API_URL = "http://127.0.0.1:5000/recommend";

    @GetMapping("/")
    public String home()
    {
       return"/home";
    }

    @GetMapping("/user-order")
    public String orderitems(Model m,Principal p)
    {
        Users loggeduser = getLoggedInUserDetails(p);
        List<ProductOrder> orders= orderService.getAllOrder(loggeduser.getId());
        m.addAttribute("orders", orders);
        return "/user/orderitems";
    }
    @GetMapping("/update-order-status")
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
        return "redirect:/user/user-order";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m)
    {
        if(p!=null)
        {
            String email = p.getName();
            Users user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
            Integer countCart = cartService.getCountCart(user.getId());
            m.addAttribute("countCart", countCart);
        }
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session)
        {
            Cart savecart = cartService.saveCart(pid, uid);
            if(ObjectUtils.isEmpty(savecart))
            {
                session.setAttribute("errorMsg", "Something went wrong while saving product");
            }
            else
            {
                session.setAttribute("succMsg", "Product added to cart");
            }
            return "redirect:/proddetail/"+pid;
    }

    @GetMapping("/cart")
    public String Loadcart(Principal p, Model m) {
        Users user = getLoggedInUserDetails(p);
        List<CartItem> carts = cartService.getCartsbyUser(user.getId());
        if (carts == null) {
            carts = new ArrayList<>();
        }

        double totalOrderPrice = 0.0;
        for (CartItem item : carts) {
            totalOrderPrice += item.getTotalUnitPrice();
        }

        m.addAttribute("carts", carts);
        m.addAttribute("totalOrderPrice", totalOrderPrice);

        Cart cart = cartService.getCartbyUser(user.getId());
        m.addAttribute("cart", cart);

        return "/user/cart";
    }


    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String update, @RequestParam Integer cid)
    {
        cartService.updateQuantity(update,cid);
        return "redirect:/user/cart";
    }

    @GetMapping("/orders")
    public String orderPage(Principal p, Model m) {
        Users user = getLoggedInUserDetails(p);
        Cart carts = cartService.getCartbyUser(user.getId());
        String firstName = "";
        String lastName = "";
        if (user.getName() != null && !user.getName().isEmpty()) {
            String[] parts = user.getName().split(" ", 2); // split into max 2 parts
            firstName = parts[0];
            if (parts.length > 1) {
                lastName = parts[1];
            }
        }
        m.addAttribute("firstName", firstName);
        m.addAttribute("lastName", lastName);

        m.addAttribute("user", user);
        m.addAttribute("carts", carts);

        Double orderPrice = carts.getTotalprice();
        Double totalOrderPrice = orderPrice + 50 + 100;

        m.addAttribute("orderPrice", orderPrice);
        m.addAttribute("totalOrderPrice", totalOrderPrice);

        return "/user/order";
    }

    @PostMapping("/proceed-order")
    public String saveOrder(@ModelAttribute OrderRequest request,
                            @ModelAttribute ProductOrder order,
                            Principal p,
                            Model m) throws Exception {

        Users user = getLoggedInUserDetails(p);
        List<CartItem> carts = cartService.getCartsbyUser(user.getId());

        boolean isCartEmpty = carts == null || carts.isEmpty();
        m.addAttribute("isCartEmpty", isCartEmpty);

        order.setUser(user);
        order.setOrderId(UUID.randomUUID().toString());


        List<OrderItem> orderItems = carts.stream().map(cart -> {
            OrderItem item = new OrderItem();
            item.setProduct(cart.getProduct());
            item.setQuantity(cart.getQuantity());
            item.setPrice(cart.getProduct().getPrice() * cart.getQuantity()); // set price per item * qty
            item.setProductOrder(order); // set back-reference if needed
            return item;
        }).toList();

        order.setItems(orderItems);

        orderService.saveOrder(order, request);
        orderService.deductStock(order);

        if ("ONLINE".equals(order.getPaymentType())) {
            try {
                String secret = "8gBm/:&EnhH.1/q";
                double totalAmount = order.getItems().stream()
                        .mapToDouble(OrderItem::getPrice)
                        .sum() +150;
                m.addAttribute("totalAmount", totalAmount);

                UUID transactionUuid = UUID.randomUUID();
                String productCode = "EPAYTEST";

                String message = String.format("total_amount=%s,transaction_uuid=%s,product_code=%s",
                        totalAmount, transactionUuid, productCode);

                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
                sha256_HMAC.init(secret_key);
                String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));

                m.addAttribute("uuid", transactionUuid);
                m.addAttribute("hash", hash);
                m.addAttribute("order", order);
            } catch (Exception e) {
                System.out.println("Payment hash generation error: " + e.getMessage());
            }

            // Recommendation logic
            StringBuilder ingredients = new StringBuilder();
            for (CartItem cart : carts) {
                if (ingredients.length() > 0) ingredients.append(", ");
                ingredients.append(cart.getProduct().getName());
            }
            List<Map<String, Object>> recommendations = getRecipeRecommendations(ingredients.toString());
            httpSession.setAttribute("recommendations", recommendations);

            return "/user/esewapay";

        } else if ("COD".equalsIgnoreCase(order.getPaymentType())) {

            // Recommendation logic
            StringBuilder ingredients = new StringBuilder();
            for (CartItem cart : carts) {
                if (ingredients.length() > 0) ingredients.append(", ");
                ingredients.append(cart.getProduct().getName());
            }
            List<Map<String, Object>> recommendations = getRecipeRecommendations(ingredients.toString());
            httpSession.setAttribute("recommendations", recommendations);

            cartService.clearCart(user.getId()); // Clear cart only on COD
            return "redirect:/user/order-recipe";
        }

        return "/user/";
    }

    @GetMapping("/order-recipe")
    public String orderRecipe(Model m) {
        // Retrieving recommendations from the session
        List<Map<String, Object>> recommendations = (List<Map<String, Object>>) httpSession.getAttribute("recommendations");
        m.addAttribute("recommendations", recommendations != null ? recommendations : List.of());
        //clearing the session attribute after use
        httpSession.removeAttribute("recommendations");
        return "/user/order-recipe";
    }

    private List<Map<String, Object>> getRecipeRecommendations(String ingredients) {
        try {
            // Prepare the JSON payload
            String jsonPayload = String.format("{\"ingredients\": \"%s\"}", ingredients);
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Create the request entity
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
            // Make the POST request to the Flask API
            ResponseEntity<List> response = restTemplate.postForEntity(FLASK_API_URL, requestEntity, List.class);

            String[] expectedFields = {
                    "recipe_name", "ingredients_list", "image_url", "calories", "directions",
                    "fat", "carbohydrates", "protein", "fiber", "vitamins"
            };

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> recommendations = response.getBody();
            if(recommendations ==null)
            {
                return List.of();
            }
            return recommendations.stream().map(recommendation -> {
                String vitaminsJson = (String) recommendation.get("vitamins");
                try {
                    if (vitaminsJson != null && !vitaminsJson.equals("N/A")) {
                        Map<String, Double> vitaminsMap = mapper.readValue(vitaminsJson, Map.class);
                        recommendation.put("vitamins", vitaminsMap);
                    } else {
                        recommendation.put("vitamins", Map.of());
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing vitamins JSON: " + e.getMessage());
                    recommendation.put("vitamins", Map.of());
                }
                return recommendation;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            // Return an empty list if the API call fails to provide the informations
            return List.of();
        }
    }
    @PostMapping("/save-produce")
    public  String saveProduce(@ ModelAttribute("produce") CustomerProduce produce, @RequestParam("PP") MultipartFile image, Principal principal, RedirectAttributes redirectAttributes)throws
        IOException
    {
        Users user = userService.getUserByEmail(principal.getName());
        produce.setUser(user);

        // Handle image
        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        produce.setProductImage(imageName);

        if (!image.isEmpty()) {
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "produce" + File.separator + imageName);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        produceService.saveProduce(produce);
        redirectAttributes.addFlashAttribute("succMsg", "Produce details submitted successfully!");
        return "redirect:/user/";

    }
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String email = principal.getName();
        Users user = userService.getUserByEmail(email);

        List<ProductOrder> orders = orderService.getAllOrder(user.getId());
        if (orders == null) {
            orders = new ArrayList<>();
        }
        Double totalSpent = orders.stream()
                .filter(order -> !order.getStatus().equals("Order in progress") &&
                        !order.getStatus().equals("Order Cancelled"))
                .mapToDouble(ProductOrder::getFinalPrice)
                .sum();

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("totalAmountSpent", totalSpent);

        return "/user/profile";
    }

    private Users getLoggedInUserDetails(Principal p)
    {
        String email = p.getName();
        Users user = userService.getUserByEmail(email);
        return user;
    }
}
