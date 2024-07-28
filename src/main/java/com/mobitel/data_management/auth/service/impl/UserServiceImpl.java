package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.*;
import com.mobitel.data_management.auth.dto.responseDto.ResponseDto;
import com.mobitel.data_management.auth.entity.token.Token;
import com.mobitel.data_management.auth.entity.token.TokenType;
import com.mobitel.data_management.auth.entity.user.Role;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.TokenRepository;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.UserService;
import com.mobitel.data_management.config.JwtService;
import com.mobitel.data_management.emailService.EmailService;
import com.mobitel.data_management.otpService.OtpStorage;
import com.mobitel.data_management.otpService.OtpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final OtpStorage otpStorage;

    @Value("${spring.application.security.user.password}")
    private String password;
    @Override
    public ResponseEntity<String> addUser(AddUserDto addUserDto) {
        Optional<User> optionalUser = userRepository.findByEmail(addUserDto.getEmail());
        if(optionalUser.isPresent()){
            return new ResponseEntity<>("Email already existed",HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setFirstname(addUserDto.getFirstname());
        user.setLastname(addUserDto.getLastname());
        user.setEmail(addUserDto.getEmail());
        user.setRole(Role.valueOf(addUserDto.getRole()));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return new ResponseEntity<>("User Added Successfully",HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> authentication(AuthDto authDto) {
        Optional<User> optionalUser = userRepository.findByEmail(authDto.getEmail());
        if(optionalUser.isPresent()){
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

            return new ResponseEntity<>(responseDto,HttpStatus.OK);
        }

        return new ResponseEntity<>("Authentication Failed", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
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

                return new ResponseEntity<>(responseDto,HttpStatus.OK);
            }
            return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> passwordReset(PasswordResetDto passwordResetDto) {
        final String email = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty())
            return new ResponseEntity<>("Invalid Authentication",HttpStatus.UNAUTHORIZED);

        User user = optionalUser.get();

        if(!passwordEncoder.matches(passwordResetDto.getPassword(), user.getPassword())){
            return new ResponseEntity<>("Password Verification Error",HttpStatus.BAD_REQUEST);
        }

        if(!passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword())){
            return new ResponseEntity<>("Password Confirmation Error",HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        userRepository.save(user);
        return new ResponseEntity<>("Password Updated",HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        Optional<User> optionalUser = userRepository.findByEmail(forgotPasswordDto.getEmail());
        if(optionalUser.isPresent()){
            String otp = OtpUtil.generateOtp();
            otpStorage.storeOtp(forgotPasswordDto.getEmail(), otp);
            emailService.sendEmail(forgotPasswordDto.getEmail(), "Your OTP Code", "Your OTP code is: " + otp);
            return new ResponseEntity<>("OTP sent to email " + forgotPasswordDto.getEmail(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid User",HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> verifyOtp(OtpDto otpDto) {
        final String otp = otpStorage.retrieveOtp(otpDto.getEmail());
        if(otp != null && otp.equals(otpDto.getOtp())){
            otpStorage.removeOtp(otpDto.getEmail());
            return new ResponseEntity<>("OTP Verified", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid OTP.", HttpStatus.BAD_GATEWAY);
    }

    @Override
    public ResponseEntity<?> newPassword(NewPasswordDto newPasswordDto) {

        Optional<User> optionalUser = userRepository.findByEmail(newPasswordDto.getEmail());
        if(optionalUser.isPresent()) {

            if (!newPasswordDto.getNewPassword().equals(newPasswordDto.getConfirmPassword())) {
                return new ResponseEntity<>("Password Confirmation Error", HttpStatus.BAD_REQUEST);
            }

            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPasswordDto.getNewPassword()));
            userRepository.save(user);
            return new ResponseEntity<>("Password Update Successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid User",HttpStatus.FORBIDDEN);

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
