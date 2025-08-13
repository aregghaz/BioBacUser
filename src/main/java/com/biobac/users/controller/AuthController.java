package com.biobac.users.controller;

import com.biobac.users.request.UserRegisterRequest;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.service.AuthService;
import com.biobac.users.request.AuthRequest;
import com.biobac.users.response.AuthResponse;
import com.biobac.users.entity.User;
import com.biobac.users.service.UserService;

import com.biobac.users.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        AuthResponse authResponse = authService.login(request);

        return ResponseUtil.success(
                "Login successful",
                authResponse
        );
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid UserRegisterRequest user) {
        userService.userRegister(user);
        return ResponseUtil.success(
                "User registered successfully"
        );
    }
}
