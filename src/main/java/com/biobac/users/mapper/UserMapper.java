package com.biobac.users.mapper;

import com.biobac.users.entity.User;
import com.biobac.users.request.UserCreateRequest;
import com.biobac.users.response.UserResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class, UserGroupMapper.class})
public abstract class UserMapper {

    @Autowired
    protected UserGroupMapper userGroupMapper;

    @Mapping(target = "positionId", source = "position.id")
    @Mapping(target = "positionName", source = "position.name")
    @Mapping(target = "userGroups", ignore = true)
    public abstract UserResponse toResponse(User entity);

    public abstract User toEntity(UserCreateRequest request);

    @AfterMapping
    protected void mapUserGroups(User entity, @MappingTarget UserResponse response) {
        response.setUserGroups(userGroupMapper.toGroupedDtoList(entity.getUserGroups()));
    }
}
