package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> authentication(AuthDto authDto);

    ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> forgotPassword(ForgotPasswordDto forgotPasswordDto);

    ResponseEntity<?> verifyOtp(OtpDto otpDto);

    ResponseEntity<?> newPassword(NewPasswordDto newPasswordDto);
}
