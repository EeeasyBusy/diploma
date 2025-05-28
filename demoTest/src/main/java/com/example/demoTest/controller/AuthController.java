package com.example.demoTest.controller;

import com.example.demoTest.entities.ErrorResponse;
import com.example.demoTest.entities.LoginRequest;
import com.example.demoTest.entities.LoginResponse;
import com.example.demoTest.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cloud'")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
        log.info("AuthController инициализирован");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Попытка входа для имени пользователя: {}", request.getUsername());
        try {
            if (authService.validateUser(request.getUsername(), request.getPassword())) {
                log.info("Успешная аутентификация пользователя: {}", request.getUsername());
                LoginResponse response = new LoginResponse("jwt_token", "Успешная авторизация");
                log.debug("Сгенерированный токен для пользователя: {}", request.getUsername());
                return ResponseEntity.ok(response);
            } else {
                log.warn("Неудачная попытка аутентификации пользователя: {}", request.getUsername());
                ErrorResponse error = new ErrorResponse("Неверный логин или пароль", 400);
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception exception) {
            log.error("Ошибка аутентификации для пользователя {}: {}", request.getUsername(), exception.getMessage(), exception);
            ErrorResponse error = new ErrorResponse("Внутренняя ошибка сервера", 500);
            return ResponseEntity.internalServerError().body(error);
    }
}}