package com.projectfyp.service;

import com.projectfyp.model.Users;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    public Users saveUser(Users users);

    public List<Users> getAllUsers(String role);

    public Boolean deleteUser(Integer id);

    public Users getUserByEmail(String email);

    public Users updateUser(Users users, MultipartFile file);
}