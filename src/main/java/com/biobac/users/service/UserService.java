package com.biobac.users.service;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.request.ChangePasswordRequest;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.request.UserCreateRequest;
import com.biobac.users.request.UserUpdateRequest;
import com.biobac.users.response.UserResponse;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface UserService {
    @Transactional
    UserResponse createUser(UserCreateRequest request);

    UserDetails loadUserByUsername(String username);

    @Transactional(readOnly = true)
    UserResponse getUserByUsername(String username);

    @Transactional(readOnly = true)
    UserResponse getById(Long id);

    @Transactional(readOnly = true)
    List<UserResponse> listAllUsers();

    @Transactional(readOnly = true)
    Pair<List<UserResponse>, PaginationMetadata> listUsersPaginated(Map<String, FilterCriteria> filters, int page, int size, String sortBy, String sortDir);

    @Transactional
    UserResponse updateUser(Long userId, UserUpdateRequest updateRequest);

    @Transactional
    UserResponse updateUserByAdmin(Long userId, UserCreateRequest updateRequest);

    @Transactional
    void changePassword(String username, ChangePasswordRequest request);
}
