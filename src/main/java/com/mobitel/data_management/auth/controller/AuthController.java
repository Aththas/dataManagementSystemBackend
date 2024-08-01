package com.mobitel.data_management.auth.controller;

import com.mobitel.data_management.auth.dto.requestDto.*;
import com.mobitel.data_management.auth.service.AuthService;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> authentication(@RequestBody AuthDto authDto){
        return authService.authentication(authDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<?>> refresh(HttpServletRequest request,HttpServletResponse response){
        return authService.refresh(request,response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto){
        return authService.forgotPassword(forgotPasswordDto);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@RequestBody OtpDto otpDto){
        return authService.verifyOtp(otpDto);
    }

    @PostMapping("/new-password")
    public ResponseEntity<ApiResponse<?>> newPassword(@RequestBody NewPasswordDto newPasswordDto){
        return authService.newPassword(newPasswordDto);
    }
}
