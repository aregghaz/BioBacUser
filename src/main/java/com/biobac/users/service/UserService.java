package com.biobac.users.service;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.request.ChangePasswordRequest;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.request.UserCreateRequest;
import com.biobac.users.request.UserUpdateRequest;
import com.biobac.users.response.UserSingleResponse;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface UserService {
    @Transactional
    UserSingleResponse createUser(UserCreateRequest request);

    UserDetails loadUserByUsername(String username);

    @Transactional(readOnly = true)
    UserSingleResponse getUserByUsername(String username);

    @Transactional(readOnly = true)
    UserSingleResponse getById(Long id);

    @Transactional(readOnly = true)
    List<UserSingleResponse> listAllUsers();

    @Transactional(readOnly = true)
    Pair<List<UserSingleResponse>, PaginationMetadata> listUsersPaginated(Map<String, FilterCriteria> filters, int page, int size, String sortBy, String sortDir);

    @Transactional
    UserSingleResponse updateUser(Long userId, UserUpdateRequest updateRequest);

    @Transactional
    void changePassword(String username, ChangePasswordRequest request);
}
