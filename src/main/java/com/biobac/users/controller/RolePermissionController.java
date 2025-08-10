package com.biobac.users.controller;

import com.biobac.users.dto.RolePermissionsDto;
import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RoleRepository roleRepository;

    @GetMapping("/permissions")
    public List<RolePermissionsDto> getRolePermissions() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> new RolePermissionsDto(
                        role.getName(),
                        role.getPermissions() == null ? List.of() : role.getPermissions().stream().map(Permission::getName).sorted().collect(Collectors.toList())
                ))
                .sorted((a, b) -> a.getRoleName().compareToIgnoreCase(b.getRoleName()))
                .collect(Collectors.toList());
    }
}
