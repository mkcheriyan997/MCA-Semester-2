package com.mkc.lifeload.service;

import com.mkc.lifeload.dto.UserDTO;
import com.mkc.lifeload.dto.UserUpdateDTO;
import com.mkc.lifeload.entity.User; // Keep User entity for method parameters that need it

import java.util.List;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);

    void deleteUser(Long id);
}