package com.biobac.users.service.impl;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Position;
import com.biobac.users.entity.User;
import com.biobac.users.exception.DuplicateException;
import com.biobac.users.exception.NotFoundException;
import com.biobac.users.mapper.UserMapper;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.PositionRepository;
import com.biobac.users.repository.UserRepository;
import com.biobac.users.request.ChangePasswordRequest;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.request.UserCreateRequest;
import com.biobac.users.request.UserUpdateRequest;
import com.biobac.users.response.UserResponse;
import com.biobac.users.service.UserService;
import com.biobac.users.utils.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateException("Username already exists: " + request.getUsername());
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("Email already exists: " + request.getEmail());
        }
        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new NotFoundException("Position not found with id: " + request.getPositionId()));

        Set<Permission> permissions = new HashSet<>();

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            permissions.addAll(permissionRepository.findAllById(request.getPermissionIds()));
        }


        User user = userMapper.toEntity(request);
        user.setActive(true);
        user.setPassword(request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : null);
        user.setPosition(position);
        user.setPermissions(permissions);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getPermissions() != null) {
            authorities.addAll(
                    user.getPermissions().stream()
                            .map(Permission::getName)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet())
            );
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Pair<List<UserResponse>, PaginationMetadata> listUsersPaginated(Map<String, FilterCriteria> filters, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = UserSpecification.buildSpecification(filters);

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserResponse> content = userPage.getContent()
                .stream()
                .map(userMapper::toResponse)
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

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equalsIgnoreCase(user.getEmail())) {
            userRepository.findByEmail(updateRequest.getEmail())
                    .filter(found -> !found.getId().equals(userId))
                    .ifPresent(found -> {
                        throw new DuplicateException("Email already exists: " + updateRequest.getEmail());
                    });
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getFirstname() != null) {
            user.setFirstname(updateRequest.getFirstname());
        }
        if (updateRequest.getLastname() != null) {
            user.setLastname(updateRequest.getLastname());
        }

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserByAdmin(Long userId, UserCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new NotFoundException("Position not found with id: " + request.getPositionId()));

        Set<Permission> permissions = new HashSet<>();

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            permissions.addAll(permissionRepository.findAllById(request.getPermissionIds()));
        }

        user.setUsername(request.getUsername());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setDob(request.getDob());
        user.setActive(true);
        user.setPassword(request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : null);
        user.setPosition(position);
        user.setPermissions(permissions);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));

        if (user.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be changed for this account");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
