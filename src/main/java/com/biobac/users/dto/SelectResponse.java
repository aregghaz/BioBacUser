package com.biobac.users.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SelectResponse {
    private Long id;
    private String name;
}

