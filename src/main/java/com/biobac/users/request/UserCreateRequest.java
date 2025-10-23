package com.biobac.users.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class UserCreateRequest {
    private String username;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String email;
    private String password;
    private LocalDate dob;
    private Long positionId;
    private Set<Long> permissionIds;
}
