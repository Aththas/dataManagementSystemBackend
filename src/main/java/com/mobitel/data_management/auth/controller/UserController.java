package com.mobitel.data_management.auth.controller;

import com.mobitel.data_management.auth.dto.requestDto.*;
import com.mobitel.data_management.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody AddUserDto addUserDto){
        return userService.addUser(addUserDto);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestParam Integer id, @RequestBody AddUserDto addUserDto){
        return userService.updateUser(id,addUserDto);
    }

    @GetMapping("/viewUser")
    public ResponseEntity<?> viewUser(@RequestParam Integer id){
        return userService.viewUser(id);
    }

    @GetMapping("/viewUsers")
    public ResponseEntity<?> viewUsers(){
        return userService.viewUsers();
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authentication(@RequestBody AuthDto authDto){
        return userService.authentication(authDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(HttpServletRequest request,HttpServletResponse response){
        return userService.refresh(request,response);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordResetDto passwordResetDto){
        return userService.passwordReset(passwordResetDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto){
        return userService.forgotPassword(forgotPasswordDto);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpDto otpDto){
        return userService.verifyOtp(otpDto);
    }

    @PostMapping("/new-password")
    public ResponseEntity<?> newPassword(@RequestBody NewPasswordDto newPasswordDto){
        return userService.newPassword(newPasswordDto);
    }
}
