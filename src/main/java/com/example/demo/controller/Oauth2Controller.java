package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.payload.response.JWTTokenSuccessResponse;
import com.example.demo.security.JWTTokenProvider;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class Oauth2Controller {


    private final JWTTokenProvider jwtTokenProvider;
    private final UserService userService;
    @Value("${jwt.token-prefix}")
    public String TOKEN_PREFIX;

    @GetMapping
    public RedirectView  authenticateUserGoogle(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        OAuth2User principal = oAuth2AuthenticationToken.getPrincipal();
        String email = principal.getAttribute("email");
        User user1 = userService.findUserByEmail(email);
        String name = principal.getAttribute("name");
        if(name==null){
            name = email;
        }
        if (user1 == null) {
            user1 = new User();
            user1.setUserName(name);
            user1.setEmail(email);
            userService.saveUserGoogle(user1);
        }
        SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);

        String jwt = TOKEN_PREFIX + " " + jwtTokenProvider.generateToken
                (new UsernamePasswordAuthenticationToken(user1, null, new ArrayList<>()));
       return new RedirectView("http://localhost:4200/login?jwt="+jwt);

    }
}
