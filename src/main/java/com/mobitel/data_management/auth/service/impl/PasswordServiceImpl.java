package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.PasswordResetDto;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.PasswordService;
import com.mobitel.data_management.other.validator.ObjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectValidator<PasswordResetDto> passwordResetValidator;
    @Override
    public ResponseEntity<?> passwordReset(PasswordResetDto passwordResetDto) {
        passwordResetValidator.validate(passwordResetDto);
        if(passwordResetDto != null){
            try{
                final String email = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
                Optional<User> optionalUser = userRepository.findByEmail(email);
                if(optionalUser.isEmpty()){
                    log.error("Invalid Authentication for Password Reset: User Not Found");
                    return new ResponseEntity<>("Invalid Authentication", HttpStatus.UNAUTHORIZED);
                }

                User user = optionalUser.get();

                if(!passwordEncoder.matches(passwordResetDto.getPassword(), user.getPassword())){
                    log.error("Password Reset: Password Verification Error");
                    return new ResponseEntity<>("Password Verification Error",HttpStatus.OK);
                }

                if(!passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword())){
                    log.error("Password Reset: Password Confirmation Error");
                    return new ResponseEntity<>("Password Confirmation Error",HttpStatus.OK);
                }

                user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
                userRepository.save(user);

                log.info("Password Reset: Password Updated for user " + user.getEmail());
                return new ResponseEntity<>("Password Updated",HttpStatus.OK);
            } catch (Exception e){
                log.error("Password Reset: " + e);
                return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Password Reset: passwordResetDto object is null");
            return new ResponseEntity<>("Null Values Not Permitted",HttpStatus.OK);
        }

    }
}
