package com.example.demoTest.controller;

import com.example.demoTest.entities.File;
import com.example.demoTest.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping()
public class Controller {
    @Autowired
    private FileService fileService;

    public Controller(FileService fileService){
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        fileService.uploadFile(file, principal.getName());
        return ResponseEntity.ok("Успешно загружен");
    }

    @GetMapping("/files")
    public ResponseEntity<List<File>> getFiles(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<File> files = fileService.getFilesByUser(principal.getName());
        return ResponseEntity.ok(files);
    }

}