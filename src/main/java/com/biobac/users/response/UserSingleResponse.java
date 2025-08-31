package com.biobac.users.response;

import com.biobac.users.dto.RolePermissionsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserSingleResponse {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String email;
    private Boolean active;
    private List<RolePermissionsDto> rolesPermissions;
}
