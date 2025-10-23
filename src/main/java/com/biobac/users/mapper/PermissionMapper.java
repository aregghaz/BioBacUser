package com.biobac.users.mapper;

import com.biobac.users.entity.Permission;
import com.biobac.users.response.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toResponse(Permission entity);
}
