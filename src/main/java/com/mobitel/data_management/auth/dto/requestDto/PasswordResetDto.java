package com.mobitel.data_management.auth.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordResetDto {

    @NotEmpty(message = "Empty Password")
    @NotNull(message = "Invalid Password")
    private String password;

    @NotEmpty(message = "Empty New Password")
    @NotNull(message = "Invalid New Password")
    private String newPassword;

    @NotEmpty(message = "Empty Confirm Password")
    @NotNull(message = "Invalid Confirm Password")
    private String confirmPassword;
}
