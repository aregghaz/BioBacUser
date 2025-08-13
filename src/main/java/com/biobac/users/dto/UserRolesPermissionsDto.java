package com.biobac.users.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class UserRolesPermissionsDto {
    private Long id;
    private String username;
    private List<RolePermissionsDto> roles;
}
