package com.biobac.users.response;

import com.biobac.users.dto.PermissionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PositionResponse {
    private Long id;
    private String name;
    private List<PermissionDto> permissions;
}
