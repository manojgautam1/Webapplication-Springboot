package com.projectfyp.service;

import com.projectfyp.model.OrderRequest;
import com.projectfyp.model.ProductOrder;

import java.util.List;

public interface OrderService {
    public void saveOrder(ProductOrder order, OrderRequest orderRequest);

    //for order viewing of specific users only
    public List<ProductOrder> getAllOrder(Integer userid) ;
    public Boolean updateOrderStatus(Integer id, String status);

    public List<ProductOrder> findOrdersByProductName(String keyword);
    //for admin to showcase all the orders
    public List<ProductOrder> getAllOrders();
    void deductStock(ProductOrder order);
}
