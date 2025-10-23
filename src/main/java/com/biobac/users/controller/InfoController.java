package com.biobac.users.controller;

import com.biobac.users.response.ApiResponse;
import com.biobac.users.response.UserResponse;
import com.biobac.users.service.UserService;
import com.biobac.users.utils.JwtUtil;
import com.biobac.users.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class InfoController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfileInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseUtil.error(
                    "Authorization header is missing or invalid"
            );
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return ResponseUtil.success(
                "Profile information retrieved successfully",
                userService.getUserByUsername(username)
        );
    }
}
