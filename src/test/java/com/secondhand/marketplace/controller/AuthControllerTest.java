package com.secondhand.marketplace.controller;

import com.secondhand.marketplace.model.AuthRequest;
import com.secondhand.marketplace.model.AuthResponse;
import com.secondhand.marketplace.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;


    @Test
    public void testAuthenticate_Success() {
        AuthRequest authRequest = new AuthRequest("testUser", "password");
        String token = "testToken";
        long expirationTime = 3600L;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtil.generateToken(authRequest.getUsername())).thenReturn(token);
        when(jwtUtil.getExpirationTime()).thenReturn(expirationTime);

        ResponseEntity<AuthResponse> response = authController.authenticate(authRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(token, response.getBody().getToken());
        assertEquals(expirationTime, response.getBody().getExpirationTime());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        AuthRequest authRequest = new AuthRequest("testUser", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<AuthResponse> response = authController.authenticate(authRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody().getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testAuthenticate_UnexpectedError() {
        AuthRequest authRequest = new AuthRequest("testUser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<AuthResponse> response = authController.authenticate(authRequest);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("An unexpected error occurred", response.getBody().getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
