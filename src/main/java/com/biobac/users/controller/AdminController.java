package com.biobac.users.controller;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.dto.UserRolesPermissionsDto;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.response.UserSingleResponse;
import com.biobac.users.service.RolePermissionService;
import com.biobac.users.service.UserService;
import com.biobac.users.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final RolePermissionService rolePermissionService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_READ')")
    @GetMapping("/users")
    public ApiResponse<List<UserRolesPermissionsDto>> listUsersWithRolesAndPermissions() {
        List<UserRolesPermissionsDto> users = userService.listUsersWithRolesAndPermissions();

        Map<String, Object> metadata = Map.of("totalUsers", users.size());

        return ResponseUtil.success("Users fetched successfully", users, metadata);
    }

    @PostMapping("/users/pagination")
    public ApiResponse<List<UserSingleResponse>> listUsersWithRolesAndPermissionsPaginated(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestBody Map<String, FilterCriteria> filters) {

        Pair<List<UserSingleResponse>, PaginationMetadata> result =
                userService.listUsersWithRolesAndPermissionsPaginated(filters, page, size, sortBy, sortDir);
        return ResponseUtil.success("Users fetched successfully", result.getFirst(), result.getSecond());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_READ')")
    @GetMapping("/users/{userId}")
    public ApiResponse<UserRolesPermissionsDto> getUserWithRolesAndPermissions(@PathVariable Long userId) {
        UserRolesPermissionsDto userDto = userService.getUserWithRolesAndPermission(userId);
        return ResponseUtil.success("User fetched successfully", userDto);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_CREATE') or hasAuthority('USER_UPDATE')")
    @PostMapping("/users/{userId}/roles/{roleName}")
    public ApiResponse<String> assignRoleToUser(@PathVariable Long userId, @PathVariable String roleName) {
        userService.assignRoleToUser(userId, roleName);
        return ResponseUtil.success("Role assigned to user successfully");
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_DELETE') or hasAuthority('USER_UPDATE')")
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

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_READ') or hasAuthority('ROLE_CREATE') or hasAuthority('ROLE_UPDATE')")
    @PostMapping("/users/{userId}/roles")
    public ApiResponse<String> editUserRoles(@PathVariable Long userId, @RequestBody List<Integer> roles) {
        userService.editUserRoles(userId, roles);
        return ResponseUtil.success("User roles updated successfully");
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_CREATE') or hasAuthority('ROLE_UPDATE')")
    @PostMapping("/roles/{roleId}/permissions")
    public ApiResponse<String> editRolePermissions(@PathVariable Long roleId, @RequestBody List<Integer> permissions) {
        rolePermissionService.editRolePermissions(roleId, permissions);
        return ResponseUtil.success("Role permissions updated successfully");
    }
}
