package com.biobac.users.service;

import com.biobac.users.dto.RolePermissionsDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RolePermissionService {
    @Transactional(readOnly = true)
    List<RolePermissionsDto> getRolePermissions();
}
