package com.projectfyp.service.impl;

import com.projectfyp.model.Cart;
import com.projectfyp.model.CartItem;
import com.projectfyp.model.Product;
import com.projectfyp.model.Users;
import com.projectfyp.repository.CartItemRepository;
import com.projectfyp.repository.CartRepository;
import com.projectfyp.repository.ProductRepository;
import com.projectfyp.repository.UserRepository;
import com.projectfyp.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart getCartbyUser(Integer userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        CartItem existingItem = null;

        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + 1;
            existingItem.setQuantity(newQuantity);
            existingItem.setUnitPrice(product.getDiscountPrice());
            existingItem.setTotalUnitPrice(newQuantity * product.getDiscountPrice());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(1);
            newItem.setUnitPrice(product.getDiscountPrice());
            newItem.setTotalUnitPrice(product.getDiscountPrice());
            newItem.setCart(cart);
            cartItemRepository.save(newItem);
        }

        // Update cart total
        List<CartItem> updatedItems = cartItemRepository.findByCartId(cart.getId());
        double totalCartPrice = updatedItems.stream()
                .mapToDouble(CartItem::getTotalUnitPrice)
                .sum();
        cart.setTotalprice(totalCartPrice);

        return cartRepository.save(cart);
    }

    @Override
    public List<CartItem> getCartsbyUser(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) return null;

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        for (CartItem item : cartItems) {
            double itemTotal = item.getProduct().getDiscountPrice() * item.getQuantity();
            item.setTotalUnitPrice(itemTotal);
        }

        return cartItems;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer totalQuantity = cartItemRepository.sumQuantityByUserId(userId);
        return (totalQuantity == null) ? 0 : totalQuantity;
    }

    @Override
    public void deleteCart(List<Cart> cartList) {
        if (!ObjectUtils.isEmpty(cartList)) {
            cartRepository.deleteAll(cartList);
        }
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
            cartItemRepository.deleteAll(items);
            cart.setTotalprice(0.0);
            cartRepository.save(cart);
        }
    }

    @Override
    public void updateQuantity(String update, Integer cid) {
        Optional<CartItem> optionalItem = cartItemRepository.findById(cid);
        if (optionalItem.isEmpty()) return;

        CartItem cartItem = optionalItem.get();
        int currentQty = cartItem.getQuantity();

        if ("sub".equalsIgnoreCase(update)) {
            int newQty = currentQty - 1;
            if (newQty <= 0) {
                cartItemRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(newQty);
                cartItem.setTotalUnitPrice(newQty * cartItem.getUnitPrice());
                cartItemRepository.save(cartItem);
            }
        } else if ("add".equalsIgnoreCase(update)) {
            int newQty = currentQty + 1;
            cartItem.setQuantity(newQty);
            cartItem.setTotalUnitPrice(newQty * cartItem.getUnitPrice());
            cartItemRepository.save(cartItem);
        } else {
            throw new IllegalArgumentException("Invalid update action: " + update);
        }

        // Update cart total
        Cart cart = cartItem.getCart();
        List<CartItem> updatedItems = cartItemRepository.findByCartId(cart.getId());
        double total = updatedItems.stream()
                .mapToDouble(CartItem::getTotalUnitPrice)
                .sum();
        cart.setTotalprice(total);
        cartRepository.save(cart);
    }
}
