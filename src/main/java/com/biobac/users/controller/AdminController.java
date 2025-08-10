package com.biobac.users.controller;

import com.biobac.users.dto.UserRolesPermissionsDto;
import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.entity.User;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.RoleRepository;
import com.biobac.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_READ')")
    @GetMapping("/users")
    public List<UserRolesPermissionsDto> listUsersWithRolesAndPermissions() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_READ')")
    @GetMapping("/users/{userId}")
    public UserRolesPermissionsDto getUserWithRolesAndPermissions(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_WRITE')")
    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long userId, @PathVariable String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(role);
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_DELETE')")
    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // Ensure role exists (return 404 if it doesn't)
        roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            boolean changed = user.getRoles().removeIf(r -> r != null && r.getName() != null && r.getName().equalsIgnoreCase(roleName));
            if (changed) {
                userRepository.save(user);
            }
        }
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_WRITE')")
    @PostMapping("/roles/{roleName}/permissions/{permissionName}")
    public ResponseEntity<Void> assignPermissionToRole(@PathVariable String roleName, @PathVariable String permissionName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found"));

        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        role.getPermissions().add(permission);
        roleRepository.save(role);
        return ResponseEntity.noContent().build();
    }

    private UserRolesPermissionsDto toDto(User user) {
        List<String> roleNames = user.getRoles() == null ? List.of() :
                user.getRoles().stream()
                        .map(Role::getName)
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());

        Set<String> permSet = new java.util.HashSet<>();
        if (user.getRoles() != null) {
            for (Role r : user.getRoles()) {
                if (r.getPermissions() != null) {
                    for (Permission p : r.getPermissions()) {
                        if (p != null && p.getName() != null) permSet.add(p.getName());
                    }
                }
            }
        }
        List<String> permNames = permSet.stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());

        return new UserRolesPermissionsDto(
                user.getId(),
                user.getUsername(),
                roleNames,
                permNames
        );
    }
}
