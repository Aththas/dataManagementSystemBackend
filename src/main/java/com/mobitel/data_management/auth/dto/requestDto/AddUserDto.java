package com.mobitel.data_management.auth.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddUserDto {

    @NotEmpty(message = "Empty Firstname")
    @NotNull(message = "Invalid Firstname")
    private String firstname;

    @NotEmpty(message = "Empty Lastname")
    @NotNull(message = "Invalid Lastname")
    private String lastname;

    @NotEmpty(message = "Empty Email")
    @NotNull(message = "Invalid Email")
    private String email;

    @NotEmpty(message = "Empty Role")
    @NotNull(message = "Invalid Role")
    private String role;
}
