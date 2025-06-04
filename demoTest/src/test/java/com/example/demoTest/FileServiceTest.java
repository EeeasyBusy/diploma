package com.example.demoTest;

import com.example.demoTest.entities.User;
import com.example.demoTest.repositories.UserRepository;
import com.example.demoTest.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FileService fileService;

    @Test
    void uploadUserFile_WhenUserNotExist() {
        Long userId = -1L;
        MultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> fileService.uploadUserFile(userId, file));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Пользователь не найден", exception.getReason());

    }

    @Test
    void uploadUserFile__WhenFileEmpty() {
        Long userId = 1L;
        MultipartFile emptyFile = new MockMultipartFile(
                "empty.txt",
                new byte[0]
        );

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileService.uploadUserFile(userId, emptyFile));

        assertEquals("Файл не выбран", exception.getMessage());

    }

}
