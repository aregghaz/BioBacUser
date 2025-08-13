package com.biobac.users.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PermissionRequest {
    @NotEmpty(message = "At least one permission is required")
    private String permissionName;
}
