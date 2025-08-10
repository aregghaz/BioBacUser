package com.biobac.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesPermissionsDto {
    private Long id;
    private String username;
    private List<String> roles;
    private List<String> permissions;
}
