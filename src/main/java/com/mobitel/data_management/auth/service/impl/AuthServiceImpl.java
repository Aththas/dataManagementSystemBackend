package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.*;
import com.mobitel.data_management.auth.dto.responseDto.ResponseDto;
import com.mobitel.data_management.auth.entity.token.Token;
import com.mobitel.data_management.auth.entity.token.TokenType;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.TokenRepository;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.AuthService;
import com.mobitel.data_management.config.JwtService;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.other.emailService.EmailService;
import com.mobitel.data_management.other.otpService.OtpStorage;
import com.mobitel.data_management.other.otpService.OtpUtil;
import com.mobitel.data_management.other.validator.ObjectValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final OtpStorage otpStorage;
    private final ObjectValidator<AuthDto> authenticationValidator;
    private final ObjectValidator<ForgotPasswordDto> forgotPasswordValidator;
    private final ObjectValidator<OtpDto> otpValidator;
    private final ObjectValidator<NewPasswordDto> newPasswordValidator;

    @Override
    public ResponseEntity<ApiResponse<?>> authentication(AuthDto authDto) {
        authenticationValidator.validate(authDto);
        if(authDto != null){
            try{
                Optional<User> optionalUser = userRepository.findByEmail(authDto.getEmail());
                if(optionalUser.isPresent()){
                    if(authDto.getEmail().endsWith("null"))
                    {
                        log.error("Authentication: User Account has been restricted");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "User Account has been restricted, Contact Admin to get back", "AUTH_ERROR_002"),
                                HttpStatus.OK);
                    }
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authDto.getEmail(),
                                    authDto.getPassword()
                            )
                    );

                    User user = optionalUser.get();
                    final String accessToken = jwtService.generateToken(user);
                    final String refreshToken = jwtService.generateRefreshToken(user);
                    revokeAllValidUserTokens(user.getId());
                    saveToken(accessToken ,user);

                    ResponseDto responseDto = new ResponseDto();
                    responseDto.setAccessToken(accessToken);
                    responseDto.setRefreshToken(refreshToken);

                    log.info("Authentication: Success - " + authDto.getEmail() + " - " +user.getRole().name());
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, responseDto, user.getRole().name(), null),
                            HttpStatus.OK);
                }
                log.error("Authentication: Failed");
                return new ResponseEntity<>(new ApiResponse<>(false, null, "username or password incorrect", "AUTH_ERROR_001"),
                        HttpStatus.OK);
            }catch (BadCredentialsException e){
                log.error("Authentication: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "username or password incorrect", "CREDENTIAL_ERROR_500"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("Authentication: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Authentication: authDto object is null");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> refresh(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            log.error("Refresh Token: Invalid Access Token Type");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Invalid Access Token Type", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);
        if(userEmail != null){
            User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
            if(jwtService.isTokenValid(jwt,user)){
                final String accessToken = jwtService.generateRefreshToken(user);
                revokeAllValidUserTokens(user.getId());
                saveToken(accessToken ,user);

                ResponseDto responseDto = new ResponseDto();
                responseDto.setAccessToken(accessToken);
                responseDto.setRefreshToken(jwt);

                log.info("Refresh Token: Generated - " + userEmail);
                return new ResponseEntity<>(
                        new ApiResponse<>(true, responseDto, "Token Generated", null),
                        HttpStatus.OK);
            }
            log.error("Refresh Token: Token Expired");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Token Expired", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
        log.error("Refresh Token: Invalid Access Token");
        return new ResponseEntity<>(
                new ApiResponse<>(false, null, "Invalid Access Token", "AUTH_ERROR_001"),
                HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        forgotPasswordValidator.validate(forgotPasswordDto);
        if(forgotPasswordDto != null){
            try{
                Optional<User> optionalUser = userRepository.findByEmail(forgotPasswordDto.getEmail());
                if(optionalUser.isPresent()){
                    if(forgotPasswordDto.getEmail().endsWith("null"))
                    {
                        log.error("Forgot Password: User Account has been restricted");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "User Account has been restricted, Contact Admin to get back", "AUTH_ERROR_002"),
                                HttpStatus.OK);
                    }
                    String otp = OtpUtil.generateOtp();
                    otpStorage.storeOtp(forgotPasswordDto.getEmail(), otp);
                    emailService.sendEmail(forgotPasswordDto.getEmail(), "Your OTP Code", "Your OTP code is: " + otp);

                    log.info("Forgot password: OTP send to email - " + forgotPasswordDto.getEmail());
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, null, "OTP sent to email " + forgotPasswordDto.getEmail(), null),
                            HttpStatus.OK);
                }
                log.error("Forgot password: Invalid User Email");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Invalid User Email", "EMAIL_ERROR_001"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("Forgot password: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Forgot password: forgotPasswordDto object is null");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> verifyOtp(OtpDto otpDto) {
        otpValidator.validate(otpDto);
        if(otpDto != null){
            try{
                final String otp = otpStorage.retrieveOtp(otpDto.getEmail());
                if(otp != null && otp.equals(otpDto.getOtp())){
                    otpStorage.removeOtp(otpDto.getEmail());

                    log.info("Verify Otp: OTP Verified");
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, null, "OTP Verified", null),
                            HttpStatus.OK);
                }
                log.error("Verify Otp: Invalid OTP");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Invalid OTP", "OTP_ERROR_001"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error(e.toString());
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Verify Otp: otpDto object is null");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> newPassword(NewPasswordDto newPasswordDto) {
        newPasswordValidator.validate(newPasswordDto);
        if(newPasswordDto != null){
            try{
                Optional<User> optionalUser = userRepository.findByEmail(newPasswordDto.getEmail());
                if(optionalUser.isPresent()) {

                    if (!newPasswordDto.getNewPassword().equals(newPasswordDto.getConfirmPassword())) {
                        log.error("New Password: Password Confirmation Error");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Password Confirmation Error", "AUTH_ERROR_003"),
                                HttpStatus.OK);
                    }

                    if (newPasswordDto.getNewPassword().length() < 8) {
                        log.error("Password Reset: Password must be at least 8 characters long");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Password must be at least 8 characters long", "AUTH_ERROR_003"),
                                HttpStatus.OK);
                    }

                    if (!newPasswordDto.getNewPassword().matches(".*[A-Z].*")) {
                        log.error("Password Reset: Password must contain at least one uppercase letter");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Password must contain at least one uppercase letter", "AUTH_ERROR_003"),
                                HttpStatus.OK);
                    }

                    if (!newPasswordDto.getNewPassword().matches(".*[a-z].*")) {
                        log.error("Password Reset: Password Confirmation Error");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Password must contain at least one lowercase letter", "AUTH_ERROR_003"),
                                HttpStatus.OK);
                    }

                    if (!newPasswordDto.getNewPassword().matches(".*[0-9].*")) {
                        log.error("Password Reset: Password Confirmation Error");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Password must contain at least one digit", "AUTH_ERROR_003"),
                                HttpStatus.OK);
                    }

                    if (!newPasswordDto.getNewPassword().matches(".*[@$!%*?&].*")) {
                        log.error("Password Reset: Password Confirmation Error");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Password must contain at least one special character (e.g., @, $, !, %, *, ?, &)", "AUTH_ERROR_003"),
                                HttpStatus.OK);
                    }

                    if (newPasswordDto.getNewPassword().contains(" ")) {
                        log.error("Password Reset: Password Confirmation Error");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Password must not contain whitespace", "AUTH_ERROR_003"),
                                HttpStatus.OK);
                    }

                    User user = optionalUser.get();
                    user.setPassword(passwordEncoder.encode(newPasswordDto.getNewPassword()));
                    userRepository.save(user);

                    log.info("New Password: Password Updated for user " + user.getEmail());
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, null, "Password Updated", null),
                            HttpStatus.OK);
                }
                log.error("New Password: Invalid User");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Invalid User", "AUTH_ERROR_001"),
                        HttpStatus.FORBIDDEN);
            }catch (Exception e){
                log.error("New Password: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("New Password: newPasswordDto object is null");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> userInfo(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            log.error("User Details: Invalid Access Token Type");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Invalid Access Token Type", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);
        if(userEmail != null){
            User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
            if(jwtService.isTokenValid(jwt,user) && jwtService.isTokenNotRevoked(jwt)){

                log.info("User Details: Found for - " + userEmail);
                return new ResponseEntity<>(
                        new ApiResponse<>(true, user, "User Found", null),
                        HttpStatus.OK);
            }
            log.error("User Details: Token Expired");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Token Expired", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
        log.error("User Details: Invalid Access Token");
        return new ResponseEntity<>(
                new ApiResponse<>(false, null, "Invalid Access Token", "AUTH_ERROR_001"),
                HttpStatus.UNAUTHORIZED);
    }

    private void saveToken(String accessToken, User user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    private void revokeAllValidUserTokens(Integer id) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUserId(id);

        if(validTokens.isEmpty())
            return;

        validTokens.forEach(
                token -> token.setRevoked(true)
        );
        tokenRepository.saveAll(validTokens);
    }
}
