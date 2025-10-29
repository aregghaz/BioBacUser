package com.biobac.users.service.impl;

import com.biobac.users.dto.PaginationMetadata;
import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Position;
import com.biobac.users.entity.User;
import com.biobac.users.exception.NotFoundException;
import com.biobac.users.mapper.PermissionMapper;
import com.biobac.users.mapper.PositionMapper;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.PositionRepository;
import com.biobac.users.request.FilterCriteria;
import com.biobac.users.request.PositionRequest;
import com.biobac.users.response.PermissionResponse;
import com.biobac.users.response.PositionResponse;
import com.biobac.users.service.PositionService;
import com.biobac.users.utils.specifications.PositionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public PositionResponse getById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position not found with id: " + id));
        return positionMapper.toResponse(position);
    }

    @Override
    @Transactional
    public PositionResponse create(PositionRequest request) {
        Position position = new Position();
        position.setName(request.getName());
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> perms = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            position.setPermissions(perms);
        } else {
            position.setPermissions(new HashSet<>());
        }
        Position saved = positionRepository.save(position);
        return positionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PositionResponse update(Long id, PositionRequest request) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position not found with id: " + id));
        List<User> users = position.getUsers();
        if (request.getName() != null) {
            position.setName(request.getName());
        }
        if (request.getPermissionIds() != null) {
            Set<Permission> perms = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            position.setPermissions(perms);
            users.forEach(u -> {
                u.setPermissions(new HashSet<>(perms));
            });
        }
        Position saved = positionRepository.save(position);
        return positionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new NotFoundException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionResponse> getAll() {
        return positionRepository.findAll().stream()
                .map(positionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Pair<List<PositionResponse>, PaginationMetadata> getPagination(Map<String, FilterCriteria> filters, Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy == null ? "id" : sortBy).ascending() : Sort.by(sortBy == null ? "id" : sortBy).descending();
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size, sort);

        Specification<Position> spec = PositionSpecification.buildSpecification(filters);
        Page<Position> positionPage = positionRepository.findAll(spec, pageable);

        List<PositionResponse> content = positionPage.getContent().stream()
                .map(positionMapper::toResponse)
                .collect(Collectors.toList());

        PaginationMetadata metadata = new PaginationMetadata(
                positionPage.getNumber(),
                positionPage.getSize(),
                positionPage.getTotalElements(),
                positionPage.getTotalPages(),
                positionPage.isLast(),
                filters,
                sortDir,
                sortBy,
                "positionTable"
        );

        return Pair.of(content, metadata);
    }

    private PositionResponse toResponse(Position entity) {
        if (entity == null) return null;

        PositionResponse response = positionMapper.toResponse(entity);

        List<PermissionResponse> perms = (entity.getPermissions() == null)
                ? new ArrayList<>()
                : entity.getPermissions().stream()
                .map(permissionMapper::toResponse).toList();

        response.setPermissions(perms);
        return response;
    }
}
