package com.mby.myStore.DTO;

import lombok.Getter;

@Getter
public class LoginResponse {
    // Getters
    private String token;
    private UserResponse user;

    public LoginResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }
}