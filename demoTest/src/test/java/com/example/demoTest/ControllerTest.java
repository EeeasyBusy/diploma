package com.example.demoTest;

import com.example.demoTest.controller.Controller;
import com.example.demoTest.entities.User;
import com.example.demoTest.repositories.FileRepository;
import com.example.demoTest.repositories.UserRepository;
import com.example.demoTest.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private UserService UserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileRepository fileRepository;
    @InjectMocks
    private Controller controller;

    @Test
    void hello_ReturnHello() {
        String response = controller.hello();

        assertEquals("Hello!", response);
    }

    @Test
    void createUser_ReturnCreatedUser() {
        User user = new User();
        user.setUserName("testUser");
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("testUser");

        when(UserService.createUser(user)).thenReturn(savedUser);
        ResponseEntity<User> response = controller.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
        verify(UserService, times(1)).createUser(user);
    }

    @Test
    void updateUser_ReturnUpdatedUser() {
        Long userId = 1L;
        User userDetails = new User();
        userDetails.setUserName("updatedUser");
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("updatedUser");

        when(UserService.updateUser(userId, userDetails)).thenReturn(updatedUser);
        ResponseEntity<User> response = controller.updateUser(userId, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        verify(UserService, times(1)).updateUser(userId, userDetails);
    }

    @Test
    void getAllUsers_ReturnListOfUsers() {
        List<User> users = Arrays.asList(new User(1L, "user1", "123"), new User(2L, "user2", "1234"));
        when(UserService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = controller.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(UserService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_ReturnUser() {
        Long userId = 1L;
        User user = new User(userId, "user1", "123");
        when(UserService.getUserById(userId)).thenReturn(user);

        ResponseEntity<User> response = controller.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(UserService, times(1)).getUserById(userId);
    }

    @Test
    void deleteUser_WhenDeletionFails() {
        Long userId = 1L;
        doThrow(new RuntimeException("Ошибка удаления пользователя"))
                .when(UserService)
                .deleteUser(userId);

        assertThrows(ResponseStatusException.class, () -> controller.deleteUser(userId));
    }
}
