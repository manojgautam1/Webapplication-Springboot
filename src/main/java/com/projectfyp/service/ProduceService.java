package com.projectfyp.service;

import com.projectfyp.model.CustomerProduce;
import com.projectfyp.model.Users;

import java.util.List;

public interface ProduceService {
    CustomerProduce saveProduce(CustomerProduce produce);
    List<CustomerProduce> getProduceByUser(Users user);
    List<CustomerProduce> getAllProduce();
}
