package com.example.demoTest.service;

import com.example.demoTest.entities.User;
import com.example.demoTest.repositories.FileRepository;
import com.example.demoTest.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    public User createUser(User user) {
        log.info("Попытка создать пользователя с именем: {}", user.getUserName());
        try {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            log.debug("Пароль пользователя успешно зашифрован: {}", user.getUserName());

            User savedUser = userRepository.save(user);
            log.info("Пользователь успешно создан с ID: {}", savedUser.getId());
            return savedUser;
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            log.error("Не удалось создать пользователя: имя пользователя '{}' возможно уже существует. Ошибка: {}", user.getUserName(), dataIntegrityViolationException.getMessage());
            throw new RuntimeException("Имя пользователя уже существует");
        } catch (Exception exception) {
            log.error("Непредвиденная ошибка при создании пользователя: {}", exception.getMessage(), exception);
            throw new RuntimeException("Не удалось создать пользователя.");
        }
    }

    @Transactional
    public User updateUser(Long id, User user) {
        log.info("Попытка обновить пользователя с ID: {}", id);
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Пользователь с ID: {} не найден", id);
                        return new RuntimeException("Пользователь с id: " + id + " не найден");
                    });

            if (user.getUserName() != null) {
                log.debug("Обновление имени пользователя с '{}' на '{}'",
                        existingUser.getUserName(), user.getUserName());
                existingUser.setUserName(user.getUserName());
            }

            User updatedUser = userRepository.save(existingUser);
            log.info("Пользователь с ID: {} успешно обновлен", id);
            return updatedUser;
        } catch (Exception e) {
            log.error("Ошибка обновления пользователя с ID: {}. Ошибка: {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить пользователя.");
        }
    }

    public List<User> getAllUsers() {
        log.info("Извлечение всех пользователей");
        try {
            List<User> users = userRepository.findAll();
            log.debug("Найдено {} пользователей", users.size());
            return users;
        } catch (Exception exception) {
            log.error("Ошибка при извлечении всех пользователей: {}", exception.getMessage(), exception);
            throw new RuntimeException("Не удалось получить пользователей.");
        }
    }

    public User getUserById(long id) {
        log.debug("Извлечение пользователя по ID: {}", id);
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("User not found with ID: {}", id);
                        return new RuntimeException("Пользователь с id: " + id + " не найден");
                    });
        } catch (Exception exception) {
            log.error("Ошибка при получении пользователя с ID: {}. Ошибка: {}", id, exception.getMessage(), exception);
            throw new RuntimeException("Не удалось получить пользователя");
        }
    }

    public void deleteUser(Long id) {
        log.info("Попытка удалить пользователя с помощью ID: {}", id);
        try {
            if (!userRepository.existsById(id)) {
                log.warn("Удаление не удалось: пользователь с ID {} не найден", id);
                throw new RuntimeException("Пользователь не найден");
            }

            userRepository.deleteById(id);
            log.info("Пользователь с ID: {} успешно удален", id);
        } catch (Exception exception) {
            log.error("Ошибка удаления пользователя с ID: {}. Ошибка: {}", id, exception.getMessage(), exception);
            throw new RuntimeException("Не удалось удалить пользователя.");
        }
    }
}