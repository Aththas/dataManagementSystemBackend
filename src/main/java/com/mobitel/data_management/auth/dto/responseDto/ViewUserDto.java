package com.mobitel.data_management.auth.dto.responseDto;

import lombok.Data;

@Data
public class ViewUserDto {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
}
