package com.biobac.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    // Role names, e.g., ROLE_USER, ROLE_ADMIN
    private Set<String> roles;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}