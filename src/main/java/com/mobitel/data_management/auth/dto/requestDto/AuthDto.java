package com.mobitel.data_management.auth.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthDto {

    @NotEmpty(message = "Empty Email")
    @NotNull(message = "Invalid Email")
    private String email;

    @NotEmpty(message = "Empty Password")
    @NotNull(message = "Invalid Password")
    private String password;
}
