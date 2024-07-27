package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.auth.dto.requestDto.AuthDto;
import com.mobitel.data_management.auth.dto.responseDto.ResponseDto;
import com.mobitel.data_management.auth.entity.user.Role;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.UserService;
import com.mobitel.data_management.config.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

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

            ResponseDto responseDto = new ResponseDto();
            responseDto.setAccessToken(accessToken);

            return new ResponseEntity<>(responseDto,HttpStatus.OK);
        }

        return new ResponseEntity<>("Authentication Failed", HttpStatus.UNAUTHORIZED);
    }
}
