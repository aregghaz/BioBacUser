package com.biobac.users.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UserRegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    @Valid
    @NotEmpty(message = "At least one role is required")
    private Set<RoleRequest> roles;
}
