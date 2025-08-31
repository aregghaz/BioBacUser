package com.biobac.users.controller;

import com.biobac.users.request.ChangePasswordRequest;
import com.biobac.users.request.UserUpdateRequest;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.response.UserSingleResponse;
import com.biobac.users.service.UserService;
import com.biobac.users.utils.JwtUtil;
import com.biobac.users.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_UPDATE')")
    @PutMapping("/{userId}")
    public ApiResponse<UserSingleResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        UserSingleResponse updated = userService.updateUser(userId, updateRequest);
        return ResponseUtil.success("User updated successfully", updated);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                              HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseUtil.error("Authorization header is missing or invalid");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        userService.changePassword(username, request);
        return ResponseUtil.success("Password changed successfully");
    }
}
