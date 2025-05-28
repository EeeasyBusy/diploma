package com.example.demoTest.entities;
import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private int status;

    public ErrorResponse(String error, int status) {
    }
}