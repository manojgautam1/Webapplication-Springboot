package com.projectfyp.service.impl;

import com.projectfyp.constants.StatusOrder;
import com.projectfyp.model.*;
import com.projectfyp.repository.CartRepository;
import com.projectfyp.repository.OrderRepository;
import com.projectfyp.repository.ProductRepository;
import com.projectfyp.service.CartService;
import com.projectfyp.service.OrderService;
import com.projectfyp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Override
    public void saveOrder(ProductOrder order, OrderRequest orderRequest) {
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());
        order.setStatus(StatusOrder.ORDER_PROGRESS.getName());
        order.setPaymentType(orderRequest.getPaymentType());

        // Set order address
        OrderAddress address = new OrderAddress();
        address.setFirstName(orderRequest.getFirstName());
        address.setLastName(orderRequest.getLastName());
        address.setEmail(orderRequest.getEmail());
        address.setMobileNo(orderRequest.getMobileNo());
        address.setAddress(orderRequest.getAddress());
        address.setCity(orderRequest.getCity());
        address.setState(orderRequest.getState());
        address.setPincode(orderRequest.getPincode());

        order.setOrderAddress(address);

        // Get cart items of the user
        List<CartItem> cartItems = cartService.getCartsbyUser(order.getUser().getId());
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getTotalUnitPrice());
            item.setProductOrder(order);

            orderItems.add(item);

            // Reduce product stock
            productService.reduceStock(cartItem.getProduct(), cartItem.getQuantity());
        }

        order.setItems(orderItems);

        // Save order and clear cart
        orderRepository.save(order);
        cartService.clearCart(order.getUser().getId());
    }
    @Override
    public List<ProductOrder> getAllOrder(Integer userid) {
        return orderRepository.findByUserId(userid);
    }

    @Override
    public Boolean updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status);
            orderRepository.save(productOrder);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }
    @Override
    public void deductStock(ProductOrder order) {
        for (OrderItem item : order.getItems()) {
            productService.reduceStock(item.getProduct(), item.getQuantity());
        }
    }
    @Override
    public List<ProductOrder> findOrdersByProductName(String keyword) {
        List<ProductOrder> allOrders = orderRepository.findAll();
        String lowerKeyword = keyword.toLowerCase();
        return allOrders.stream()
                .filter(order -> order.getItems().stream()
                        .anyMatch(item -> item.getProduct().getName().toLowerCase().contains(keyword.toLowerCase())))
                .toList();
    }
}
