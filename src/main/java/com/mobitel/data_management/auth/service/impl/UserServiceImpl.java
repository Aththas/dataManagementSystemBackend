package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.auth.entity.user.Role;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.UserService;
import com.mobitel.data_management.other.mapper.UserMapper;
import com.mobitel.data_management.other.validator.ObjectValidator;
import lombok.RequiredArgsConstructor;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ObjectValidator<AddUserDto> addUserValidator;

    @Value("${spring.application.security.user.password}")
    private String password;
    @Override
    public ResponseEntity<String> addUser(AddUserDto addUserDto) {
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
    }

    @Override
    public ResponseEntity<String> updateUser(Integer id, AddUserDto addUserDto) {
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
    }

    @Override
    public ResponseEntity<?> viewUser(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            return new ResponseEntity<>(userMapper.userViewMapper(user),HttpStatus.OK);
        }
        return new ResponseEntity<>("User Not Found",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> viewUsers() {
        List<User> users = userRepository.findAllByOrderByIdAsc();
        if(users.isEmpty())
            return new ResponseEntity<>("User List is Empty",HttpStatus.OK);

        return new ResponseEntity<>(users.stream().map(userMapper::userViewMapper).collect(Collectors.toList()), HttpStatus.OK);
    }
}
