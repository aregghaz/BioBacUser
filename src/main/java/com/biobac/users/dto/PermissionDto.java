package com.biobac.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionDto {
    private String permissionName;
    private Long permissionId;
}
