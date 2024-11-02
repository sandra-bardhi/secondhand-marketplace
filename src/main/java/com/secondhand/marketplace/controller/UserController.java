package com.secondhand.marketplace.controller;

import com.secondhand.marketplace.dto.UserRegistrationDTO;
import com.secondhand.marketplace.entity.User;
import com.secondhand.marketplace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {


    private final UserService userService;

    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDTO userDto) {
        User user= modelMapper.map(userDto, User.class);
        userService.registerUser(user);
        log.info("User registered", userDto.getUsername());
        return ResponseEntity.ok("User registered successfully");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUnauthorizedActionException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
