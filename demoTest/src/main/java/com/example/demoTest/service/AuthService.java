package com.example.demoTest.service;

import com.example.demoTest.entities.User;
import com.example.demoTest.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        log.info("AuthService инициализирован с BCryptPasswordEncoder");
    }

    public boolean validateUser(String userName, String password) {
        log.debug("Попытка проверки пользователя: {}", userName);

        try {
            Optional<User> user = userRepository.findByUserName(userName);

            if (user.isEmpty()) {
                log.warn("Пользователь не найден: {}", userName);
                return false;
            }

            boolean passwordMatches = passwordEncoder.matches(password, user.get().getPassword());

            if (passwordMatches) {
                log.info("Успешная аутентификация пользователя: {}", userName);
            } else {
                log.warn("Несоответствие пароля для пользователя: {}", userName);
            }

            return passwordMatches;

        } catch (Exception exception) {
            log.error("Ошибка аутентификации для пользователя {}: {}", userName, exception.getMessage(), exception);
            throw new RuntimeException("Ошибка аутентификации", exception);
        }
    }
}