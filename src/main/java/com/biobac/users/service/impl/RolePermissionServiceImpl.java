package com.biobac.users.service.impl;

import com.biobac.users.dto.RolePermissionsDto;
import com.biobac.users.dto.PermissionDto;
import com.biobac.users.entity.Role;
import com.biobac.users.repository.RoleRepository;
import com.biobac.users.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RolePermissionsDto> getRolePermissions() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> new RolePermissionsDto(
                        role.getName(),
                        role.getPermissions() == null ? List.of() : role.getPermissions().stream()
                                .filter(p -> p != null && p.getName() != null)
                                .map(p -> new PermissionDto(p.getName()))
                                .sorted((p1, p2) -> p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName()))
                                .collect(Collectors.toList())
                ))
                .sorted((a, b) -> a.getRoleName().compareToIgnoreCase(b.getRoleName()))
                .collect(Collectors.toList());
    }
}
