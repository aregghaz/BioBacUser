package com.biobac.users.service;

import com.biobac.users.dto.RolePermissionsDto;
import com.biobac.users.dto.SelectResponse;
import com.biobac.users.entity.GroupType;
import com.biobac.users.response.PermissionResponse;

import java.util.List;
import java.util.Map;

public interface RolePermissionService {
    List<RolePermissionsDto> getRolePermissions();

    void editRolePermissions(Long roleId, List<Integer> permissions);

    List<SelectResponse> getAllRoles();

    List<SelectResponse> getAllPermissions();

    Map<String, List<PermissionResponse>> getGroupPermissions();

    List<Long> getAccessGroupIds(Long userId, GroupType groupType);
}
