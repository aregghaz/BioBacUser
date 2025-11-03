package com.biobac.users.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserUpdateRequest {
    private String firstname;
    private String lastname;
    private String phoneNumber;
    @Email(message = "Email should be valid")
    private String email;
    private LocalDateTime dob;
}
