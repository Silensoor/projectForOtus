package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.ERole;
import com.example.demo.exceptions.UserExistException;
import com.example.demo.payload.request.SignupRq;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User createUser(SignupRq signupRq) {
        User user = new User();
        user.setEmail(signupRq.getEmail());
        user.setUserName(signupRq.getUsername());
        user.setName(signupRq.getFirstname());
        user.setLastName(signupRq.getLastname());
        user.setPassword(passwordEncoder.encode(signupRq.getPassword()));
        user.getRole().add(ERole.ROLE_USER);
        try {
            log.info("Saving User {}", signupRq.getEmail());
            return userRepository.saveAndFlush(user);
        } catch (Exception e) {
            log.error("Error during registration {}", e.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exist. Please check credentials");
        }
    }
    public void saveUserGoogle(User user){
        userRepository.saveAndFlush(user);
    }

    public User findUserByEmail(String email) {
       return userRepository.findUserByEmail(email).orElse(null);
    }
    public User findUserByUserName(String userName){
        return userRepository.findUserByUserName(userName).orElse(null);
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setBio(userDTO.getBio());
        return userRepository.saveAndFlush(user);
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal) {
        String userName = principal.getName();
        return userRepository.findUserByEmail(userName).
                orElseThrow(() -> new UsernameNotFoundException("UserName not found " + userName));
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
