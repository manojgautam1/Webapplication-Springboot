package com.projectfyp.repository;

import com.projectfyp.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Integer> {
    public Users findByEmail(String email);

    public List<Users> findByRole(String role);
}
