package com.example.demoTest.controller;

import com.example.demoTest.entities.User;
import com.example.demoTest.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class Controller {
    @Autowired
    private final FileService fileService;

    @Autowired
    public Controller(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello!";
    }

    @PostMapping("/new_user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = fileService.createUser(user);
        return ResponseEntity.status(201).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable int id,
            @RequestBody User userDetails
    ) {
        User updatedUser = fileService.updateUser((long) id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/files")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = fileService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = fileService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        fileService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void ignoreFavicon() {
    }

}