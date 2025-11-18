package com.biobac.users.service.impl;

import com.biobac.users.dto.RolePermissionsDto;
import com.biobac.users.dto.SelectResponse;
import com.biobac.users.entity.*;
import com.biobac.users.exception.NotFoundException;
import com.biobac.users.mapper.PermissionMapper;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.RoleRepository;
import com.biobac.users.repository.UserGroupRepository;
import com.biobac.users.repository.UserRepository;
import com.biobac.users.response.PermissionResponse;
import com.biobac.users.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.biobac.users.utils.PermissionDictionary;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public List<RolePermissionsDto> getRolePermissions() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> new RolePermissionsDto(
                        role.getName(),
                        role.getId(),
                        role.getPermissions() == null ? List.of() : role.getPermissions().stream()
                                .filter(p -> p != null && p.getName() != null)
                                .map(permissionMapper::toResponse).toList()
                ))
                .sorted((a, b) -> a.getRoleName().compareToIgnoreCase(b.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void editRolePermissions(Long roleId, List<Integer> permissions) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));
        Set<Permission> managedPermissions = new HashSet<>();
        if (!permissions.isEmpty()) {
            for (Integer permissionId : permissions) {
                if (permissionId == null) {
                    continue;
                }
                Permission permission = permissionRepository.findById(permissionId.longValue())
                        .orElseThrow(() -> new NotFoundException("Permission not found with id: " + permissionId));
                managedPermissions.add(permission);
            }
        }
        role.setPermissions(managedPermissions);
        roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SelectResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> SelectResponse.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SelectResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permission -> SelectResponse.builder()
                        .id(permission.getId())
                        .name(permission.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<PermissionResponse>> getGroupPermissions() {
        List<PermissionResponse> permissionResponses = permissionRepository.findAll().stream()
                .map(permissionMapper::toResponse)
                .toList();

        Map<String, String> customPermissionGroupMap = PermissionDictionary.SPECIAL_GROUPS;

        Map<String, List<PermissionResponse>> grouped = permissionResponses.stream()
                .collect(Collectors.groupingBy(p -> {
                    if (p.getName() != null) {
                        String customGroup = customPermissionGroupMap.get(p.getName());
                        if (customGroup != null) return customGroup;
                    }

                    if (p.getTitle() != null && !p.getTitle().trim().isEmpty()) {
                        String t = p.getTitle().trim();
                        int spaceIdx = t.indexOf(' ');
                        if (spaceIdx > 0 && spaceIdx < t.length() - 1) {
                            return t.substring(spaceIdx + 1).trim();
                        }
                        return t;
                    }

                    String name = p.getName() == null ? "" : p.getName();
                    String[] parts = name.split("_");
                    if (parts.length == 0) return "Прочее";

                    Set<String> opPrefixes = Set.of("READ", "CREATE", "UPDATE", "DELETE");
                    int start = (opPrefixes.contains(parts[0].toUpperCase(Locale.ROOT)) && parts.length > 1) ? 1 : 0;

                    String entityKey = parts[start].toUpperCase(Locale.ROOT);
                    if (start + 2 < parts.length) {
                        String three = (parts[start] + "_" + parts[start + 1] + "_" + parts[start + 2]).toUpperCase(Locale.ROOT);
                        if (PermissionDictionary.ENTITY_RU.containsKey(three)) entityKey = three;
                    }
                    if (start + 1 < parts.length) {
                        String two = (parts[start] + "_" + parts[start + 1]).toUpperCase(Locale.ROOT);
                        if (PermissionDictionary.ENTITY_RU.containsKey(two)) entityKey = two;
                    }

                    return PermissionDictionary.ENTITY_RU.getOrDefault(entityKey, entityKey);
                }));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getAccessGroupIds(Long userId, GroupType groupType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<UserGroup> userGroups = userGroupRepository.findByUserAndGroupType(user, groupType);
        return userGroups.stream().map(UserGroup::getGroupId).toList();
    }
}
