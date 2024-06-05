package com.example.demo.facade;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    public UserDTO userToUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstname(user.getName());
        userDTO.setLastname(user.getLastName());
        userDTO.setBio(user.getBio());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }
}
