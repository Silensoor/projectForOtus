package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.payload.request.LoginRq;
import com.example.demo.payload.request.SignupRq;
import com.example.demo.payload.response.JWTTokenSuccessResponse;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.security.JWTTokenProvider;
import com.example.demo.service.UserService;
import com.example.demo.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class AuthController {

    private final ResponseErrorValidation responseErrorValidation;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    @Value("${jwt.token-prefix}")
    public String TOKEN_PREFIX;


    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRq signupRq, BindingResult bindingResult) {
        ResponseEntity<Object> objectResponseEntity = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(objectResponseEntity)) return objectResponseEntity;
        userService.createUser(signupRq);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRq loginRq, BindingResult bindingResult) {
        ResponseEntity<Object> objectResponseEntity = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(objectResponseEntity)) return objectResponseEntity;
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRq.getUsername(),
                loginRq.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = TOKEN_PREFIX + " " + jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }


}
