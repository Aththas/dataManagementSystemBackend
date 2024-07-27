package com.mobitel.data_management.auth.dto.requestDto;

import lombok.Data;

@Data
public class AddUserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String role;
}
