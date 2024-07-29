package com.mobitel.data_management.auth.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OtpDto {

    @NotEmpty(message = "Empty Email")
    @NotNull(message = "Invalid Email")
    private String email;

    @NotEmpty(message = "Empty Otp")
    @NotNull(message = "Invalid Otp")
    private String otp;
}
