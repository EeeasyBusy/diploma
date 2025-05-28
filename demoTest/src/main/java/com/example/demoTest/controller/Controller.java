package com.example.demoTest.controller;

import com.example.demoTest.entities.File;
import com.example.demoTest.entities.User;
import com.example.demoTest.repositories.FileRepository;
import com.example.demoTest.repositories.UserRepository;
import com.example.demoTest.service.FileAndUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cloud'")
public class Controller {
    @Autowired
    private final FileAndUserService fileAndUserService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    public Controller(FileAndUserService fileAndUserService) {
        this.fileAndUserService = fileAndUserService;
        log.info("Controller инициализирован");
    }

    @GetMapping("/")
    public String hello() {
        log.info("Запрос к корневому эндпоинту");
        return "Hello!";
    }

    @PostMapping("/new_user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Создание пользователя: username={}", user.getUserName());
        try {
            User savedUser = fileAndUserService.createUser(user);
            log.info("Пользователь создан: ID={}", savedUser.getId());
            return ResponseEntity.status(201).body(savedUser);
        } catch (Exception exception) {
            log.error("Ошибка при создании пользователя: {}", exception.getMessage(), exception);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
        }
    }

    @PostMapping("/{userId}/upload-file")
    public ResponseEntity<?> uploadUserFile(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        log.info("Загрузка файла для пользователя ID={}, размер файла={} байт", userId, file.getSize());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: ID={}", userId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                });
        if (file.isEmpty()) {
            log.warn("Пустой файл для пользователя ID={}", userId);
            return ResponseEntity.badRequest().body("Файл не выбран");
        }

        try {
            File newFile = new File();
            newFile.setUserId(userId);
            newFile.setFilename(file.getOriginalFilename());
            newFile.setFileData(file.getBytes());
            File savedFile = fileRepository.save(newFile);
            log.info("Файл загружен: ID={}, имя={}, пользователь ID={}", savedFile.getId(), file.getOriginalFilename(), userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Файл успешно загружен",
                    "fileId", savedFile.getId(),
                    "userId", userId
            ));

        } catch (IOException exception) {
            log.error("Ошибка загрузки файла: {}", exception.getMessage(), exception);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка загрузки файла: " + exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        log.info("Обновление пользователя: ID={}", id);
        try {

            User updatedUser = fileAndUserService.updateUser(id, userDetails);
            log.info("Пользователь обновлен: ID={}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception exception) {
            log.error("Ошибка обновления пользователя ID={}: {}", id, exception.getMessage(), exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка обновления");
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        List<User> users = fileAndUserService.getAllUsers();
        log.info("Найдено пользователей: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Запрос пользователя: ID={}", id);
        User user = fileAndUserService.getUserById(id);
        if (user == null) {
            log.warn("Пользователь не найден: ID={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        log.info("Пользователь найден: ID={}, username={}", id, user.getUserName());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Удаление пользователя: ID={}", id);
        try {
            fileAndUserService.deleteUser(id);
            log.info("Пользователь удален: ID={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception exception) {
            log.error("Ошибка удаления пользователя ID={}: {}", id, exception.getMessage(), exception);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка удаления");
        }
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void ignoreFavicon() {
        log.trace("Запрос favicon.ico игнорируется");
    }

}