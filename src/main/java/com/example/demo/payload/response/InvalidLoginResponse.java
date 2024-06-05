package com.example.demo.payload.response;

import lombok.Getter;

@Getter
public class InvalidLoginResponse {
    private final String userName;
    private final String password;
    public InvalidLoginResponse(){
        this.userName = "Invalid username";
        this.password = "Invalid password";
    }
}
