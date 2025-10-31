package com.biobac.users.controller;

import com.biobac.users.dto.SelectResponse;
import com.biobac.users.entity.GroupType;
import com.biobac.users.response.ApiResponse;
import com.biobac.users.dto.RolePermissionsDto;
import com.biobac.users.response.PermissionResponse;
import com.biobac.users.service.RolePermissionService;
import com.biobac.users.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @GetMapping("/group/permissions")
    public ApiResponse<Map<String, List<PermissionResponse>>> getGroupPermissions() {
        Map<String, List<PermissionResponse>> response = rolePermissionService.getGroupPermissions();
        return ResponseUtil.success("Permissions fetched successfully", response);
    }

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

    @GetMapping("/warehouse-groups/{userId}")
    public ApiResponse<List<Long>> getWarehouseGroupIds(@PathVariable Long userId){
        List<Long> ids = rolePermissionService.getAccessGroupIds(userId, GroupType.WAREHOUSE);
        return ResponseUtil.success("Warehouse group ids fetched successfully", ids);
    }

    @GetMapping("/company-groups/{userId}")
    public ApiResponse<List<Long>> getCompanyGroupIds(@PathVariable Long userId){
        List<Long> ids = rolePermissionService.getAccessGroupIds(userId, GroupType.COMPANY);
        return ResponseUtil.success("Warehouse group ids fetched successfully", ids);
    }

    @GetMapping("/ingredient-groups/{userId}")
    public ApiResponse<List<Long>> getIngredientGroupIds(@PathVariable Long userId){
        List<Long> ids = rolePermissionService.getAccessGroupIds(userId, GroupType.INGREDIENT);
        return ResponseUtil.success("Warehouse group ids fetched successfully", ids);
    }

    @GetMapping("/product-groups/{userId}")
    public ApiResponse<List<Long>> getProductGroupIds(@PathVariable Long userId){
        List<Long> ids = rolePermissionService.getAccessGroupIds(userId, GroupType.PRODUCT);
        return ResponseUtil.success("Warehouse group ids fetched successfully", ids);
    }
}
