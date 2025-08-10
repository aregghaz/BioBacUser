package com.biobac.users.service.impl;


import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.entity.User;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.RoleRepository;
import com.biobac.users.repository.UserRepository;
import com.biobac.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User save(User user) {
        // Resolve roles and permissions by name (find or create) to avoid transient entity issues
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> managedRoles = new HashSet<>();
            for (Role incomingRole : user.getRoles()) {
                if (incomingRole == null || incomingRole.getName() == null) {
                    continue;
                }
                Role role = roleRepository.findByName(incomingRole.getName())
                        .orElseGet(() -> {
                            Role r = new Role();
                            r.setName(incomingRole.getName());
                            return r;
                        });
                // Handle permissions for the role
                if (incomingRole.getPermissions() != null && !incomingRole.getPermissions().isEmpty()) {
                    Set<Permission> managedPerms = new HashSet<>();
                    for (Permission incomingPerm : incomingRole.getPermissions()) {
                        if (incomingPerm == null || incomingPerm.getName() == null) {
                            continue;
                        }
                        Permission perm = permissionRepository.findByName(incomingPerm.getName())
                                .orElseGet(() -> {
                                    Permission p = new Permission();
                                    p.setName(incomingPerm.getName());
                                    return p;
                                });
                        // Ensure permission is persisted and managed
                        if (perm.getId() == null) {
                            perm = permissionRepository.save(perm);
                        }
                        managedPerms.add(perm);
                    }
                    role.setPermissions(managedPerms);
                }
                // Ensure role is persisted and managed
                if (role.getId() == null) {
                    role = roleRepository.save(role);
                }
                managedRoles.add(role);
            }
            user.setRoles(managedRoles);
        }
        // Encode password if provided
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null) {
            // Add roles
            authorities.addAll(
                    user.getRoles().stream()
                            .map(Role::getName)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet())
            );
            // Add permissions from roles
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    authorities.addAll(
                            role.getPermissions().stream()
                                    .map(Permission::getName)
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toSet())
                    );
                }
            });
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
