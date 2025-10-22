package com.biobac.users.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PositionRequest {
    private String name;
    // IDs of permissions to associate with this position
    private Set<Long> permissionIds;
}
