package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.auth.dto.requestDto.AuthDto;
import com.mobitel.data_management.auth.dto.requestDto.PasswordResetDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<String> addUser(AddUserDto addUserDto);

    ResponseEntity<?> authentication(AuthDto authDto);

    ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> passwordReset(PasswordResetDto passwordResetDto);
}
