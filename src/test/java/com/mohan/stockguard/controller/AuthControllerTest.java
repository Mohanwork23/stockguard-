package com.mohan.stockguard.controller;

import com.mohan.stockguard.dto.AuthRequest;
import com.mohan.stockguard.security.JwtUtil;
import com.mohan.stockguard.security.UserDetailsImpl;
import com.mohan.stockguard.security.UserDetailsServiceImpl;
import com.mohan.stockguard.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void loginReturnsTokenWhenCredentialsAreValid() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("customer@example.com");
        request.setPassword("customerpass");

        User user = User.builder()
            .id(1L)
            .email(request.getEmail())
            .passwordHash("encoded")
            .role(User.Role.CUSTOMER)
            .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            new UserDetailsImpl(user), null, null);

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);
        Mockito.when(jwtUtil.generateToken(eq(authentication))).thenReturn("token-abc");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("token-abc"));
    }
}
