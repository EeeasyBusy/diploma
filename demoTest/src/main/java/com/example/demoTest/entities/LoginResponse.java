package com.example.demoTest.entities;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String message;

    public LoginResponse(String token, String message) {
    }
}
