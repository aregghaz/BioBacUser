package com.biobac.users.mapper;

import com.biobac.users.entity.User;
import com.biobac.users.request.UserCreateRequest;
import com.biobac.users.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface UserMapper {
    @Mapping(target = "positionId", source = "position.id")
    @Mapping(target = "positionName", source = "position.name")
    UserResponse toResponse(User entity);

    List<UserResponse> toResponseList(List<User> entities);

    User toEntity(UserCreateRequest request);
}
