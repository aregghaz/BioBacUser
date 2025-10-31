package com.biobac.users.dto;

import com.biobac.users.entity.GroupType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserGroupDto {
    private GroupType groupType;
    private List<Long> groupIds;
}
