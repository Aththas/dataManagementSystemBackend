package com.mobitel.data_management.auth.controller;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.auth.dto.requestDto.AuthDto;
import com.mobitel.data_management.auth.dto.responseDto.ResponseDto;
import com.mobitel.data_management.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody AddUserDto addUserDto){
        return userService.addUser(addUserDto);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authentication(@RequestBody AuthDto authDto){
        return userService.authentication(authDto);
    }
}
