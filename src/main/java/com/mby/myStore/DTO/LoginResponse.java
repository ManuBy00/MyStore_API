package com.mby.myStore.DTO;

import com.mby.myStore.Model.Cliente;

public class LoginResponse {
    private String token;
    private Cliente cliente;

    public LoginResponse(String token, Cliente cliente) {
        this.token = token;
        this.cliente = cliente;
    }
    // Getters
    public String getToken() { return token; }
    public Cliente getCliente() { return cliente; }
}