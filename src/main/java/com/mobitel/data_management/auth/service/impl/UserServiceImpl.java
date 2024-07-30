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
        addUserValidator.validate(addUserDto);
        if(addUserDto != null){
            try{
                Optional<User> optionalUser = userRepository.findByEmail(addUserDto.getEmail());
                if(optionalUser.isPresent()){
                    log.error("Add User: Email already existed - " + addUserDto.getEmail());
                    return new ResponseEntity<>("Email already existed", HttpStatus.OK);
                }

                User user = new User();
                user.setFirstname(addUserDto.getFirstname());
                user.setLastname(addUserDto.getLastname());
                user.setEmail(addUserDto.getEmail());
                user.setRole(Role.valueOf(addUserDto.getRole()));
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);

                log.info("Add User: New User Added - " + addUserDto.getEmail());
                return new ResponseEntity<>("User Added Successfully",HttpStatus.CREATED);
            } catch (Exception e){
                log.error("Add User: " + e);
                return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Add User: addUserDto object is null");
            return new ResponseEntity<>("Null Values Not Permitted",HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<String> updateUser(Integer id, AddUserDto addUserDto) {
        if(id != null){
            addUserValidator.validate(addUserDto);
            if(addUserDto != null){
                try{
                    Optional<User> optionalUser = userRepository.findById(id);
                    if(optionalUser.isPresent()){
                        User user = optionalUser.get();
                        user.setFirstname(addUserDto.getFirstname());
                        user.setLastname(addUserDto.getLastname());
                        user.setEmail(addUserDto.getEmail());
                        user.setRole(Role.valueOf(addUserDto.getRole()));
                        userRepository.save(user);

                        log.info("Update User: User Updated - " + addUserDto.getEmail());
                        return new ResponseEntity<>("User Updated Successfully",HttpStatus.OK);
                    }
                    log.error("Update User: User Not Found");
                    return new ResponseEntity<>("User Not Found",HttpStatus.OK);
                } catch (Exception e){
                    log.error("Update User: " + e);
                    return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else{
                log.error("Update User: addUserDto object is null");
                return new ResponseEntity<>("Null Values Not Permitted",HttpStatus.OK);
            }
        }else{
            log.error("Update User: Null User ID");
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
                    log.info("View User: User Data Retrieved - " + user.getEmail());
                    return new ResponseEntity<>(userMapper.userViewMapper(user),HttpStatus.OK);
                }
                log.error("View User: User Not Found");
                return new ResponseEntity<>("User Not Found",HttpStatus.OK);
            } catch (Exception e){
                log.error("View User: " + e);
                return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View User: Null User ID");
            return new ResponseEntity<>("Null User ID",HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> viewUsers() {
        try {
            List<User> users = userRepository.findAllByOrderByIdAsc();
            if(users.isEmpty()){
                log.error("View Users: Empty User List");
                return new ResponseEntity<>("User List is Empty",HttpStatus.OK);
            }
            log.info("View Users: " + (long) users.size() + " User Data Retrieved");
            return new ResponseEntity<>(users.stream().map(userMapper::userViewMapper).collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e){
            log.error("View Users: " + e);
            return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
