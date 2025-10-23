package com.biobac.users.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PositionRequest {
    private String name;
    private Set<Long> permissionIds;
}
