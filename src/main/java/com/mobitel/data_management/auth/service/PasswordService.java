package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.PasswordResetDto;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface PasswordService {
    ResponseEntity<ApiResponse<?>> passwordReset(PasswordResetDto passwordResetDto);
}
