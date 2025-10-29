package com.biobac.users.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PositionResponse extends AuditableResponse {
    private Long id;
    private String name;
    private List<PermissionResponse> permissions;
}
