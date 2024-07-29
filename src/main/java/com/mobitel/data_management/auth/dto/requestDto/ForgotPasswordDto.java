package com.mobitel.data_management.auth.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgotPasswordDto {

    @NotEmpty(message = "Empty Email")
    @NotNull(message = "Invalid Email")
    private String email;
}
