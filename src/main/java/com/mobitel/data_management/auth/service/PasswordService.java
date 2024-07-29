package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.PasswordResetDto;
import org.springframework.http.ResponseEntity;

public interface PasswordService {
    ResponseEntity<?> passwordReset(PasswordResetDto passwordResetDto);
}
