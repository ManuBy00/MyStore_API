package com.mby.myStore.DTO;

import com.mby.myStore.Model.User;
import lombok.Getter;

@Getter
public class LoginResponse {
    // Getters
    private String token;
    private UserDTO user;

    public LoginResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}