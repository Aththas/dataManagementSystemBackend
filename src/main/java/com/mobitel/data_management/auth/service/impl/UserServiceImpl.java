package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.auth.entity.user.Role;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.UserService;
import com.mobitel.data_management.other.mapper.UserMapper;
import com.mobitel.data_management.other.validator.ObjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ObjectValidator<AddUserDto> addUserValidator;

    @Value("${spring.application.security.user.password}")
    private String password;
    @Override
    public ResponseEntity<String> addUser(AddUserDto addUserDto) {
        if(addUserDto != null){
            try{
                addUserValidator.validate(addUserDto);
                Optional<User> optionalUser = userRepository.findByEmail(addUserDto.getEmail());
                if(optionalUser.isPresent()){
                    return new ResponseEntity<>("Email already existed", HttpStatus.OK);
                }

                User user = new User();
                user.setFirstname(addUserDto.getFirstname());
                user.setLastname(addUserDto.getLastname());
                user.setEmail(addUserDto.getEmail());
                user.setRole(Role.valueOf(addUserDto.getRole()));
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);

                return new ResponseEntity<>("User Added Successfully",HttpStatus.CREATED);
            } catch (Exception e){
                log.error(e.toString());
                return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity<>("Null Values Not Permitted",HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<String> updateUser(Integer id, AddUserDto addUserDto) {
        if(id != null){
            if(addUserDto != null){
                try{
                    addUserValidator.validate(addUserDto);
                    Optional<User> optionalUser = userRepository.findById(id);
                    if(optionalUser.isPresent()){
                        User user = optionalUser.get();
                        user.setFirstname(addUserDto.getFirstname());
                        user.setLastname(addUserDto.getLastname());
                        user.setEmail(addUserDto.getEmail());
                        user.setRole(Role.valueOf(addUserDto.getRole()));
                        userRepository.save(user);

                        return new ResponseEntity<>("User Updated Successfully",HttpStatus.OK);
                    }
                    return new ResponseEntity<>("User Not Found",HttpStatus.OK);
                } catch (Exception e){
                    log.error(e.toString());
                    return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else{
                return new ResponseEntity<>("Null Values Not Permitted",HttpStatus.OK);
            }
        }else{
            return new ResponseEntity<>("Null User ID",HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> viewUser(Integer id) {
        if(id != null){
            try{
                Optional<User> optionalUser = userRepository.findById(id);
                if(optionalUser.isPresent()){
                    User user = optionalUser.get();
                    return new ResponseEntity<>(userMapper.userViewMapper(user),HttpStatus.OK);
                }
                return new ResponseEntity<>("User Not Found",HttpStatus.OK);
            } catch (Exception e){
                log.error(e.toString());
                return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity<>("Null User ID",HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> viewUsers() {
        try {
            List<User> users = userRepository.findAllByOrderByIdAsc();
            if(users.isEmpty())
                return new ResponseEntity<>("User List is Empty",HttpStatus.OK);

            return new ResponseEntity<>(users.stream().map(userMapper::userViewMapper).collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e){
            log.error(e.toString());
            return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
