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

    public abstract UserResponse toResponse(User entity);

    public abstract User toEntity(UserCreateRequest request);

    @AfterMapping
    protected void after(User entity, @MappingTarget UserResponse response) {
        if (entity.getPosition() != null) {
            response.setPositionId(entity.getPosition().getId());
            response.setPositionName(entity.getPosition().getName());
        }
        if (entity.getUserGroups() != null) {
            response.setUserGroups(userGroupMapper.toGroupedDtoList(entity.getUserGroups()));
        }
    }
}
