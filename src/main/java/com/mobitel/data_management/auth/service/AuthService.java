package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.*;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<ApiResponse<?>> authentication(AuthDto authDto);

    ResponseEntity<ApiResponse<?>> refresh(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<ApiResponse<?>> forgotPassword(ForgotPasswordDto forgotPasswordDto);

    ResponseEntity<ApiResponse<?>> verifyOtp(OtpDto otpDto);

    ResponseEntity<ApiResponse<?>> newPassword(NewPasswordDto newPasswordDto);

    ResponseEntity<ApiResponse<?>> userInfo(HttpServletRequest request, HttpServletResponse response);
}
