package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<String> addUser(AddUserDto addUserDto);

    ResponseEntity<String> updateUser(Integer id, AddUserDto addUserDto);

    ResponseEntity<?> viewUser(Integer id);

    ResponseEntity<?> viewUsers();

    ResponseEntity<?> authentication(AuthDto authDto);

    ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> passwordReset(PasswordResetDto passwordResetDto);

    ResponseEntity<?> forgotPassword(ForgotPasswordDto forgotPasswordDto);

    ResponseEntity<?> verifyOtp(OtpDto otpDto);

    ResponseEntity<?> newPassword(NewPasswordDto newPasswordDto);
}
