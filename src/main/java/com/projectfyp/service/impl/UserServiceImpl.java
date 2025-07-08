package com.projectfyp.service.impl;

import com.projectfyp.model.Product;
import com.projectfyp.model.Users;
import com.projectfyp.repository.UserRepository;
import com.projectfyp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Users saveUser(Users user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users saveUser = userRepository.save(user);
        return saveUser;
    }

    @Override
    public List<Users> getAllUsers(String role) {

        return userRepository.findByRole(role);
    }

    @Override
    public Boolean deleteUser(Integer id) {
        return null;
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Users updateUser(Users users, MultipartFile file) {
        return null;
    }
}