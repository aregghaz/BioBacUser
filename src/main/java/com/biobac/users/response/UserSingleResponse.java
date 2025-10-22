package com.biobac.users.response;

import com.biobac.users.dto.PermissionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserSingleResponse extends AuditableResponse {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String email;
    private Boolean active;
    private Long positionId;
    private String positionName;
    private List<PermissionDto> permissions;
}
