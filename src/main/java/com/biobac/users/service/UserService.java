package com.biobac.users.service;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.dto.UserRolesPermissionsDto;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.request.UserRegisterRequest;
import com.biobac.users.response.UserSingleResponse;
import com.biobac.users.response.UserTableResponse;

import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface UserService {
    @Transactional
    void userRegister(UserRegisterRequest user);

    UserDetails loadUserByUsername(String username);

    @Transactional(readOnly = true)
    List<UserRolesPermissionsDto> listUsersWithRolesAndPermissions();

    @Transactional(readOnly = true)
    UserRolesPermissionsDto getUserWithRolesAndPermission(Long userId);

    @Transactional
    void assignRoleToUser(Long userId, String roleName);

    @Transactional
    void removeRoleFromUser(Long userId, String roleName);

    @Transactional
    void assignPermissionToRole(String roleName, String permissionName);

    @Transactional(readOnly = true)
    UserSingleResponse getUserByUsername(String username);

    @Transactional(readOnly = true)
    Pair<List<UserSingleResponse>, PaginationMetadata> listUsersWithRolesAndPermissionsPaginated(Map<String, FilterCriteria> filters, int page, int size, String sortBy, String sortDir);
}
