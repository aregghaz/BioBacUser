package com.biobac.users.service;

import com.biobac.users.dto.RolePermissionsDto;
import com.biobac.users.dto.SelectResponse;
import com.biobac.users.response.PermissionResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface RolePermissionService {
    @Transactional(readOnly = true)
    List<RolePermissionsDto> getRolePermissions();

    @Transactional
    void editRolePermissions(Long roleId, List<Integer> permissions);

    @Transactional(readOnly = true)
    List<SelectResponse> getAllRoles();

    @Transactional(readOnly = true)
    List<SelectResponse> getAllPermissions();

    @Transactional(readOnly = true)
    Map<String, List<PermissionResponse>> getGroupPermissions();
}
