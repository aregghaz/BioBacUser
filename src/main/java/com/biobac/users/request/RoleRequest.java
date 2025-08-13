package com.biobac.users.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class RoleRequest {
    @NotEmpty(message = "At least one role is required")
    private String roleName;
    @NotEmpty(message = "At least one permission is required")
    @Valid
    private Set<PermissionRequest> permissions;
}
