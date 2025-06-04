package com.example.demoTest.service;

import com.example.demoTest.entities.File;
import com.example.demoTest.entities.User;
import com.example.demoTest.repositories.FileRepository;
import com.example.demoTest.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> uploadUserFile(Long userId, MultipartFile file) throws IOException {
        log.info("Загрузка файла для пользователя ID={}, размер файла={} байт", userId, file.getSize());
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь не найден: ID={}", userId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        });
        if (file.isEmpty()) {
            log.warn("Пустой файл для пользователя ID={}", userId);
            throw new IllegalArgumentException("Файл не выбран");
        }
        File newFile = new File();
        newFile.setUserId(userId);
        newFile.setFilename(file.getOriginalFilename());
        newFile.setFileData(file.getBytes());

        File savedFile = fileRepository.save(newFile);
        log.info("Файл загружен: ID={}, имя={}, пользователь ID={}",
                savedFile.getId(), file.getOriginalFilename(), userId);

        return Map.of(
                "message", "Файл успешно загружен",
                "fileId", savedFile.getId(),
                "userId", userId
        );
    }

    @Transactional
    public void deleteFile(Long userId, Long fileId) {
        log.info("Попытка удаления файла ID={} ", fileId);
        validateUserExists(userId);

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> {
                    log.warn("Файл ID={} не найден", fileId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не найден");
                });

        fileRepository.delete(file);
        log.info("Файл ID={} успешно удален", fileId);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь не найден: ID={}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
    }
}
