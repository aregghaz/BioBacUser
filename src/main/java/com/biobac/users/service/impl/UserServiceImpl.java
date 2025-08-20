package com.biobac.users.service.impl;


import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.dto.PermissionDto;
import com.biobac.users.dto.RolePermissionsDto;
import com.biobac.users.dto.UserRolesPermissionsDto;
import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.entity.User;
import com.biobac.users.exception.DuplicateException;
import com.biobac.users.exception.NotFoundException;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.RoleRepository;
import com.biobac.users.repository.UserRepository;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.request.PermissionRequest;
import com.biobac.users.request.RoleRequest;
import com.biobac.users.request.UserRegisterRequest;
import com.biobac.users.response.UserSingleResponse;
import com.biobac.users.service.UserService;
import com.biobac.users.utils.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public void userRegister(UserRegisterRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new DuplicateException("Username already exists: " + userRequest.getUsername());
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new DuplicateException("Email already exists: " + userRequest.getEmail());
        }
        Set<Role> managedRoles = new HashSet<>();
        if (userRequest.getRoles() != null && !userRequest.getRoles().isEmpty()) {
            for (RoleRequest incomingRole : userRequest.getRoles()) {
                if (incomingRole == null || incomingRole.getRoleName() == null) {
                    continue;
                }
                Role role = roleRepository.findByName(incomingRole.getRoleName())
                        .orElseGet(() -> {
                            Role r = new Role();
                            r.setName(incomingRole.getRoleName());
                            return r;
                        });
                if (incomingRole.getPermissions() != null && !incomingRole.getPermissions().isEmpty()) {
                    Set<Permission> managedPerms = new HashSet<>();
                    for (PermissionRequest incomingPerm : incomingRole.getPermissions()) {
                        if (incomingPerm == null || incomingPerm.getPermissionName() == null) {
                            continue;
                        }
                        Permission perm = permissionRepository.findByName(incomingPerm.getPermissionName())
                                .orElseGet(() -> {
                                    Permission p = new Permission();
                                    p.setName(incomingPerm.getPermissionName());
                                    return p;
                                });
                        if (perm.getId() == null) {
                            perm = permissionRepository.save(perm);
                        }
                        managedPerms.add(perm);
                    }
                    role.setPermissions(managedPerms);
                }
                if (role.getId() == null) {
                    role = roleRepository.save(role);
                }
                managedRoles.add(role);
            }
        }
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setActive(true);
        user.setPassword(userRequest.getPassword() != null ? passwordEncoder.encode(userRequest.getPassword()) : null);
        user.setRoles(managedRoles);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
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

    @Transactional(readOnly = true)
    @Override
    public List<UserRolesPermissionsDto> listUsersWithRolesAndPermissions() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserRolesPermissionsDto getUserWithRolesAndPermission(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        return toDto(user);
    }

    @Transactional
    @Override
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found with name: " + roleName));

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        boolean alreadyHasRole = user.getRoles().stream()
                .anyMatch(r -> r != null && r.getName() != null && r.getName().equalsIgnoreCase(roleName));
        if (alreadyHasRole) {
            throw new DuplicateException("User already has role: " + roleName);
        }
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found with name: " + roleName));
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            boolean changed = user.getRoles().removeIf(r -> r != null && r.getName() != null && r.getName().equalsIgnoreCase(roleName));
            if (changed) {
                userRepository.save(user);
            }
        }
    }

    @Transactional
    @Override
    public void assignPermissionToRole(String roleName, String permissionName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found with name: " + roleName));
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new NotFoundException("Permission not found with name: " + permissionName));

        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        boolean alreadyHasPermission = role.getPermissions().stream()
                .anyMatch(p -> p != null && permissionName.equalsIgnoreCase(p.getName()));
        if (alreadyHasPermission) {
            throw new DuplicateException("Role already has permission: " + permissionName);
        }
        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSingleResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
        return toSingleResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Pair<List<UserSingleResponse>, PaginationMetadata> listUsersWithRolesAndPermissionsPaginated(Map<String, FilterCriteria> filters, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = UserSpecification.buildSpecification(filters);

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserSingleResponse> content = userPage.getContent()
                .stream()
                .map(this::toSingleResponse)
                .collect(Collectors.toList());

        PaginationMetadata metadata = new PaginationMetadata(
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast(),
                filters,
                sortDir,
                sortBy,
                "userTable"
        );

        return Pair.of(content, metadata);
    }

    private UserSingleResponse toSingleResponse(User user) {
        if (user == null) {
            return null;
        }
        List<RolePermissionsDto> roles = user.getRoles() == null ? List.of() :
                user.getRoles().stream()
                        .map(role -> {
                            List<PermissionDto> permissions = role.getPermissions() == null ? List.of() :
                                    role.getPermissions().stream()
                                            .filter(p -> p != null && p.getName() != null)
                                            .map(p -> new PermissionDto(p.getName()))
                                            .sorted((p1, p2) -> p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName()))
                                            .collect(Collectors.toList());
                            return new RolePermissionsDto(role.getName(), permissions);
                        })
                        .sorted((r1, r2) -> r1.getRoleName().compareToIgnoreCase(r2.getRoleName()))
                        .collect(Collectors.toList());

        return new UserSingleResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getActive(),
                roles
        );
    }

    private UserRolesPermissionsDto toDto(User user) {
        List<RolePermissionsDto> roles = user.getRoles() == null ? List.of() :
                user.getRoles().stream()
                        .map(role -> {
                            List<PermissionDto> permissions = role.getPermissions() == null ? List.of() :
                                    role.getPermissions().stream()
                                            .filter(p -> p != null && p.getName() != null)
                                            .map(p -> new PermissionDto(p.getName()))
                                            .sorted((p1, p2) -> p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName()))
                                            .collect(Collectors.toList());
                            return new RolePermissionsDto(role.getName(), permissions);
                        })
                        .sorted((r1, r2) -> r1.getRoleName().compareToIgnoreCase(r2.getRoleName()))
                        .collect(Collectors.toList());

        return new UserRolesPermissionsDto(
                user.getId(),
                user.getUsername(),
                roles
        );
    }
}
