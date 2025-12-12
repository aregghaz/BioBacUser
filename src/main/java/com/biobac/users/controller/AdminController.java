package com.biobac.users.controller;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.request.UserCreateRequest;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.response.UserResponse;
import com.biobac.users.service.UserService;
import com.biobac.users.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        return ResponseUtil.success("User created successfully", userService.createUser(request));
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> listUsers() {
        List<UserResponse> users = userService.listAllUsers();
        Map<String, Object> metadata = Map.of("totalUsers", users.size());
        return ResponseUtil.success("Users fetched successfully", users, metadata);
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        UserResponse userDto = userService.getById(userId);
        return ResponseUtil.success("User fetched successfully", userDto);
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserCreateRequest request) {
        UserResponse userDto = userService.updateUserByAdmin(userId, request);
        return ResponseUtil.success("User updated successfully", userDto);
    }

    @PostMapping("/pagination")
    public ApiResponse<List<UserResponse>> listUsersPaginated(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestBody(required = false) Map<String, FilterCriteria> filters) {

        Pair<List<UserResponse>, PaginationMetadata> result =
                userService.listUsersPaginated(filters, page, size, sortBy, sortDir);
        return ResponseUtil.success("Users fetched successfully", result.getFirst(), result.getSecond());
    }
}
