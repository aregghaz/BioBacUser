package com.biobac.users.mapper;

import com.biobac.users.dto.UserGroupDto;
import com.biobac.users.entity.GroupType;
import com.biobac.users.entity.UserGroup;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserGroupMapper {

    default List<UserGroupDto> toGroupedDtoList(List<UserGroup> entities) {
        if (entities == null || entities.isEmpty()) return List.of();

        Map<GroupType, List<Long>> grouped = entities.stream()
                .collect(Collectors.groupingBy(
                        UserGroup::getGroupType,
                        Collectors.mapping(UserGroup::getGroupId, Collectors.toList())
                ));

        return grouped.entrySet().stream()
                .map(e -> new UserGroupDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}

