package com.projectfyp.service.impl;

import com.projectfyp.model.CustomerProduce;
import com.projectfyp.model.Users;
import com.projectfyp.repository.ProduceRepository;
import com.projectfyp.service.ProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduceServiceImpl implements ProduceService {

    @Autowired
    private ProduceRepository produceRepository;

    @Override
    public CustomerProduce saveProduce(CustomerProduce produce) {

        return produceRepository.save(produce);
    }

    @Override
    public List<CustomerProduce> getProduceByUser(Users user) {
        return produceRepository.findByUser(user);
    }

    @Override
    public List<CustomerProduce> getAllProduce() {
        return produceRepository.findAll();
    }
}
