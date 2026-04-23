package com.mkc.lifeload.service;

import com.mkc.lifeload.dto.UserRequestDTO;
import com.mkc.lifeload.dto.UserResponseDTO;
import com.mkc.lifeload.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        // createdAt is handled by the User entity's constructor
        return user;
    }

    public UserResponseDTO toDto(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        return userResponseDTO;
    }
}