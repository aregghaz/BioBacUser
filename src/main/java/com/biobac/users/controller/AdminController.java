package com.biobac.users.controller;

import com.biobac.users.dto.UserRolesPermissionsDto;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.service.UserService;
import com.biobac.users.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_READ')")
    @GetMapping("/users")
    public ApiResponse<List<UserRolesPermissionsDto>> listUsersWithRolesAndPermissions() {
        List<UserRolesPermissionsDto> users = userService.listUsersWithRolesAndPermissions();

        Map<String, Object> metadata = Map.of("totalUsers", users.size());

        return ResponseUtil.success("Users fetched successfully", users, metadata);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_READ')")
    @GetMapping("/users/{userId}")
    public ApiResponse<UserRolesPermissionsDto> getUserWithRolesAndPermissions(@PathVariable Long userId) {
        UserRolesPermissionsDto userDto = userService.getUserWithRolesAndPermission(userId);
        return ResponseUtil.success("User fetched successfully", userDto);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_WRITE')")
    @PostMapping("/users/{userId}/roles/{roleName}")
    public ApiResponse<String> assignRoleToUser(@PathVariable Long userId, @PathVariable String roleName) {
        userService.assignRoleToUser(userId, roleName);
        return ResponseUtil.success("Role assigned to user successfully");
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_DELETE')")
    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ApiResponse<String> removeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName) {
        userService.removeRoleFromUser(userId, roleName);
        return ResponseUtil.success("Role removed from user successfully");
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_WRITE')")
    @PostMapping("/roles/{roleName}/permissions/{permissionName}")
    public ApiResponse<String> assignPermissionToRole(@PathVariable String roleName, @PathVariable String permissionName) {
        userService.assignPermissionToRole(roleName, permissionName);
        return ResponseUtil.success("Permission assigned to role successfully");
    }
}
