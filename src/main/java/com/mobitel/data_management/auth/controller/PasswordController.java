package com.mobitel.data_management.auth.controller;

import com.mobitel.data_management.auth.dto.requestDto.PasswordResetDto;
import com.mobitel.data_management.auth.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @PostMapping("/password-reset")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordResetDto passwordResetDto){
        return passwordService.passwordReset(passwordResetDto);
    }
}
