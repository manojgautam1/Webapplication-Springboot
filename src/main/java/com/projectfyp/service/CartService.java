package com.projectfyp.service;

import com.projectfyp.model.Cart;
import com.projectfyp.model.CartItem;

import java.util.List;

public interface CartService {
    public Cart saveCart(Integer productId, Integer userId);
    public List<CartItem> getCartsbyUser(Integer userId);

    public Cart getCartbyUser(Integer userId);

    public Integer getCountCart(Integer userId);
    public void deleteCart(List<Cart> cart);

    public void clearCart(Integer userId);

    public void updateQuantity(String update, Integer cid);
}
