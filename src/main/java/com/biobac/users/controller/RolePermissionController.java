package com.biobac.users.controller;

import com.biobac.users.dto.SelectResponse;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.dto.RolePermissionsDto;
import com.biobac.users.service.RolePermissionService;
import com.biobac.users.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @GetMapping("/permissions")
    public ApiResponse<List<RolePermissionsDto>> getRolePermissions() {
        List<RolePermissionsDto> rolePermissionsDtos = rolePermissionService.getRolePermissions();
        return ResponseUtil.success("Role permissions fetched successfully", rolePermissionsDtos);
    }

    @GetMapping("/all-roles")
    public ApiResponse<List<SelectResponse>> getAllRoles() {
        List<SelectResponse> roles = rolePermissionService.getAllRoles();
        return ResponseUtil.success("Roles fetched successfully", roles);
    }

    @GetMapping("/all-permissions")
    public ApiResponse<List<SelectResponse>> getAllPermissions() {
        List<SelectResponse> permissions = rolePermissionService.getAllPermissions();
        return ResponseUtil.success("Permissions fetched successfully", permissions);
    }
}
