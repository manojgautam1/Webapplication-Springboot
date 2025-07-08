package com.projectfyp.repository;

import com.projectfyp.model.CustomerProduce;
import com.projectfyp.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduceRepository extends JpaRepository<CustomerProduce, Long> {
    List<CustomerProduce> findByUser(Users user);
}
