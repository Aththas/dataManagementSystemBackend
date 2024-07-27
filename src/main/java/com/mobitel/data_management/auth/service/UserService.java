package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.auth.dto.requestDto.AuthDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<String> addUser(AddUserDto addUserDto);

    ResponseEntity<?> authentication(AuthDto authDto);
}
