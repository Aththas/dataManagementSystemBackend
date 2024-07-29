package com.mobitel.data_management.auth.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewPasswordDto {

    @NotEmpty(message = "Empty Email")
    @NotNull(message = "Invalid Email")
    private String email;

    @NotEmpty(message = "Empty New Password")
    @NotNull(message = "Invalid New Password")
    private String newPassword;

    @NotEmpty(message = "Empty Confirm Password")
    @NotNull(message = "Invalid Confirm Password")
    private String confirmPassword;
}
