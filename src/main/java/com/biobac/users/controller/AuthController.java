package com.biobac.users.controller;

import com.biobac.users.request.AuthRequest;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.response.AuthResponse;
import com.biobac.users.service.AuthService;
import com.biobac.users.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        AuthResponse authResponse = authService.login(request);

        return ResponseUtil.success(
                "Login successful",
                authResponse
        );
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseUtil.success(
                "Token refreshed successfully",
                authResponse
        );

    }
}
