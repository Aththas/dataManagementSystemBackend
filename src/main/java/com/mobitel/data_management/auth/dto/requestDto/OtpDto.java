package com.mobitel.data_management.auth.dto.requestDto;

import lombok.Data;

@Data
public class OtpDto {
    private String email;
    private String otp;
}
