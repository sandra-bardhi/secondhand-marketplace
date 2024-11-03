package com.secondhand.marketplace.controller;

import com.secondhand.marketplace.dto.UserRegistrationDTO;
import com.secondhand.marketplace.entity.User;
import com.secondhand.marketplace.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        mockMvc = standaloneSetup(userController).build();
    }

    @Test
    public void registerUser_shouldReturnSuccessMessage_whenUserIsRegistered() throws Exception {
        UserRegistrationDTO userDto = new UserRegistrationDTO("user", "password", "Full Name", "Address");
        User user = new User("user", "password", "Full Name", "Address");

        when(modelMapper.map(any(UserRegistrationDTO.class), any())).thenReturn(user);
        when(userService.registerUser(user)).thenReturn(user);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\",\"fullName\":\"Full Name\",\"address\":\"Address\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void registerUser_shouldReturnBadRequest_whenUserServiceFails() throws Exception {
        UserRegistrationDTO userDto = new UserRegistrationDTO("user", "password", "Full Name", "Address");
        User user = new User();

        when(modelMapper.map(any(UserRegistrationDTO.class), any())).thenReturn(user);
        doThrow(new RuntimeException("User registration failed")).when(userService).registerUser(user);
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\",\"fullName\":\"Full Name\",\"address\":\"Address\"}"))
                .andExpect(status().is5xxServerError()) // Expect OK if successful
                .andExpect(content().string("User registration failed"));
    }
}
