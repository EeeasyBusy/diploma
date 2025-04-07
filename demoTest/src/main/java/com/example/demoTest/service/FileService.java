package com.example.demoTest.service;

import com.example.demoTest.entities.File;
import com.example.demoTest.entities.User;
import com.example.demoTest.repositories.FileRepository;
import com.example.demoTest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    public void uploadFile(MultipartFile file, String username) {
        User user = userRepository.findByuserName(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public List<File> getFilesByUser(String username) {
        return fileRepository.findByid(userRepository.findByuserName(username).get().getId());
    }
}