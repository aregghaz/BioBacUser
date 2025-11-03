package com.biobac.users.request;

import com.biobac.users.dto.UserGroupDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserCreateRequest {
    private String username;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String email;
    private String password;
    private LocalDateTime dob;
    private Long positionId;
    private List<UserGroupDto> groups;
}
