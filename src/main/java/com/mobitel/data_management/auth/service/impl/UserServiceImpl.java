package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.auth.entity.user.Role;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.AuthService;
import com.mobitel.data_management.auth.service.UserService;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.other.emailService.EmailService;
import com.mobitel.data_management.other.mapper.UserMapper;
import com.mobitel.data_management.other.validator.ObjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final EmailService emailService;
    private final AuthService authService;

    @Value("${spring.application.security.user.password}")
    private String password;

    @Value("${spring.application.security.url}")
    private String appUrl;
    @Override
    public ResponseEntity<ApiResponse<?>> addUser(AddUserDto addUserDto) {
        addUserValidator.validate(addUserDto);
        if(addUserDto != null){
            try{

                Optional<User> optionalUser = userRepository.findByEmail(addUserDto.getEmail());
                if(optionalUser.isPresent()){
                    log.error("Add User: Email already existed - " + addUserDto.getEmail());
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Email already existed - " + addUserDto.getEmail(), "EXIST_ERROR_002"),
                            HttpStatus.OK);
                }

                if(!addUserDto.getEmail().endsWith("@mobitel.lk"))
                {
                    log.error("Add User: Only the Company associated email are allowed");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Only the Company associated email are allowed - " + addUserDto.getEmail(), "EMAIL_ERROR_002"),
                            HttpStatus.OK);
                }

                User user = new User();
                user.setFirstname(addUserDto.getFirstname());
                user.setLastname(addUserDto.getLastname());
                user.setEmail(addUserDto.getEmail());
                user.setRole(Role.valueOf(addUserDto.getRole()));
                user.setPassword(passwordEncoder.encode(password));
                user.setViewPermission(false);

                User savedUser = userRepository.save(user);
                savedUser.setGrpName("viewGrp_" + savedUser.getId());
                userRepository.save(savedUser);

                emailService.sendEmail(addUserDto.getEmail(), "Access for Mobitel Data Management System",
                        "Hi!\n" +
                                "We would like to invite you to connect to Mobitel Data Management System. " +
                                "Use the default credentials below:\n" +
                                "username: " + addUserDto.getEmail() + "\n" +
                                "password: " + password + "\n" +
                                "Please click the link below to visit the web app: \n" +
                                appUrl);

                log.info("Add User: New User Added - " + addUserDto.getEmail());
                return new ResponseEntity<>(
                        new ApiResponse<>(true, null, "User Added Successfully", null),
                        HttpStatus.OK);
            } catch (Exception e){
                log.error("Add User: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Add User: addUserDto object is null");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateUser(Integer id, AddUserDto addUserDto) {
        if(id != null){
            addUserValidator.validate(addUserDto);
            if(addUserDto != null){
                try{
                    if(!addUserDto.getEmail().endsWith("@mobitel.lk"))
                    {
                        log.error("Update User: Only the Company associated email are allowed");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Only the Company associated email are allowed - " + addUserDto.getEmail(), "EMAIL_ERROR_002"),
                                HttpStatus.OK);
                    }

                    Optional<User> optionalUser = userRepository.findById(id);
                    if(optionalUser.isPresent()){

                        List<User> optionalUserList = userRepository.findUsersByEmailExcludingCurrentUser(addUserDto.getEmail(),id);
                        if(!optionalUserList.isEmpty()){
                            log.error("Update User: Email already existed - " + addUserDto.getEmail());
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "Email already existed - " + addUserDto.getEmail(), "EMAIL_ERROR_002"),
                                    HttpStatus.OK);
                        }

                        User user = optionalUser.get();
                        user.setFirstname(addUserDto.getFirstname());
                        user.setLastname(addUserDto.getLastname());
                        user.setEmail(addUserDto.getEmail());
                        user.setRole(Role.valueOf(addUserDto.getRole()));
                        userRepository.save(user);

                        log.info("Update User: User Updated - " + addUserDto.getEmail());
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, null, "User Updated Successfully", null),
                                HttpStatus.OK);
                    }
                    log.error("Update User: User Not Found");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "User Not Found", "USER_ERROR_002"),
                            HttpStatus.OK);
                } catch (Exception e){
                    log.error("Update User: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else{
                log.error("Update User: addUserDto object is null");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
        }else{
            log.error("Update User: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewUser(Integer id) {
        if(id != null){
            try{
                Optional<User> optionalUser = userRepository.findById(id);
                if(optionalUser.isPresent()){
                    User user = optionalUser.get();
                    log.info("View User: User Data Retrieved - " + user.getEmail());
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, userMapper.userViewMapper(user), "User Data Retrieved - " + user.getEmail(), null),
                            HttpStatus.OK);
                }
                log.error("View User: User Not Found");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "User Not Found", "USER_ERROR_002"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("View User: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View User: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewUsers(int page, int size, String sortBy, boolean ascending) {
        try {
            // Create a Sort object based on the sortBy parameter and direction
            Sort sort = Sort.by(sortBy);
            sort = ascending ? sort.ascending() : sort.descending();

            // Create a Pageable object with the provided page, size, and sort
            Pageable pageable = PageRequest.of(page, size, sort);

            // Retrieve the paginated and sorted results
            Page<User> users = userRepository.findAll(pageable);
            List<User> countUsers = userRepository.findAll();
            int totalUsers = countUsers.size();
            if(users.isEmpty()){
                log.error("View Users: Empty User List");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Empty User List", "EMPTY_ERROR_500"),
                        HttpStatus.OK);
            }
            log.info("View Users: User Data Retrieved");
            return new ResponseEntity<>(
                    new ApiResponse<>(true, users.stream().map(userMapper::userViewMapper).collect(Collectors.toList()), Integer.toString(totalUsers), null),
                    HttpStatus.OK);
        } catch (Exception e){
            log.error("View Users: " + e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> disableUser(Integer id) {
        //this will disable users by adding "null" to the end of the user email
        //so first check if accounts with "null" at the end of the user email to verify if it is already disabled or not
        if(id != null){
            try{
                Optional<User> optionalUser = userRepository.findById(id);
                if(optionalUser.isPresent() && !optionalUser.get().getRole().equals(Role.ADMIN)){
                    User user = optionalUser.get();
                    if(user.getEmail().endsWith("null")){
                        log.warn("Disable User: User Already Disabled");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "User already disabled", "USER_ERROR_006"),
                                HttpStatus.OK);
                    }
                    user.setEmail(user.getEmail() + "null");
                    userRepository.save(user);

                    authService.revokeAllValidUserTokens(id);//make the logged-in users session expired

                    emailService.sendEmail(user.getEmail().substring(0,user.getEmail().length()-4), "Account Disabled for Mobitel Data Management System",
                            """
                                    Hi!
                                     Your Mobitel Data Management Account has been temporarily disabled due to security reasons\s
                                     Contact your admin and get back your account""");

                    log.info("Disable User: Disabled " + user.getEmail().substring(0,user.getEmail().length()-4));
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, null, "User Disabled", null),
                            HttpStatus.OK);
                }

                log.error("Disable User: User Not Found");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "User Not Found", "USER_ERROR_002"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("Disable User: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Disable User: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> enableUser(Integer id) {
        //this will enable users by removing "null" from the end of the user email
        //so first check if accounts with "null" at the end of the user email to verify if it is already disabled or not
        if(id != null){
            try{
                Optional<User> optionalUser = userRepository.findById(id);
                if(optionalUser.isPresent() && !optionalUser.get().getRole().equals(Role.ADMIN)){
                    User user = optionalUser.get();
                    if(user.getEmail().endsWith("null")){
                        user.setEmail(user.getEmail().substring(0,user.getEmail().length()-4));
                        userRepository.save(user);

                        emailService.sendEmail(user.getEmail(), "Account Enabled for Mobitel Data Management System",
                                """
                                        Hi!
                                        Your Mobitel Data Management Account has been enabled successfully\s
                                         """ +
                                        "Please click the link below to visit the web app: \n" +
                                        appUrl);

                        log.info("Enable User: Enabled " + user.getEmail());
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, null, "User Enabled", null),
                                HttpStatus.OK);
                    }
                    log.warn("Enable User: User Already Enabled");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "User already Enabled", "USER_ERROR_006"),
                            HttpStatus.OK);
                }

                log.error("Enable User: User Not Found");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "User Not Found", "USER_ERROR_002"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("Enable User: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Enable User: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> enableCsvPermission(Integer id) {
        if(id != null){
            try{
                Optional<User> optionalUser = userRepository.findById(id);
                if(optionalUser.isPresent() && !optionalUser.get().getRole().equals(Role.ADMIN)){
                    User user = optionalUser.get();
                    if(!user.isViewPermission()){
                        user.setViewPermission(true);
                        userRepository.save(user);

                        emailService.sendEmail(user.getEmail(), "Permission Enabled for Mobitel Data Management System",
                                """
                                        Hi!
                                        You have received permission to view excel sheets in Mobitel Data Management provided by admin\s
                                         """ +
                                        "Please click the link below to visit the web app: \n" +
                                        appUrl);

                        log.info("Enable Permission: CSV View Access Provided " + user.getEmail());
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, null, "CSV View Access Provided to " + user.getEmail(), null),
                                HttpStatus.OK);
                    }
                    log.warn("Enable Permission: User Already Has Access");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "User already has Access", "USER_ERROR_006"),
                            HttpStatus.OK);
                }

                log.error("Enable Permission: User Not Found");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "User Not Found", "USER_ERROR_002"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("Enable Permission: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Enable Permission: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> disableCsvPermission(Integer id) {
        if(id != null){
            try{
                Optional<User> optionalUser = userRepository.findById(id);
                if(optionalUser.isPresent() && !optionalUser.get().getRole().equals(Role.ADMIN)){
                    User user = optionalUser.get();
                    if(user.isViewPermission()){
                        user.setViewPermission(false);
                        userRepository.save(user);

                        emailService.sendEmail(user.getEmail(), "Permission Disabled for Mobitel Data Management System",
                                """
                                        Hi!
                                        Your view excel sheets permission has been restricted in Mobitel Data Management by admin\s
                                         """ +
                                        "Please click the link below to visit the web app: \n" +
                                        appUrl);

                        log.info("Enable Permission: CSV View Access Disabled " + user.getEmail());
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, null, "CSV View Access Disabled from " + user.getEmail(), null),
                                HttpStatus.OK);
                    }
                    log.warn("Enable Permission: User Already Has Access");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "User already has Access", "USER_ERROR_006"),
                            HttpStatus.OK);
                }

                log.error("Enable Permission: User Not Found");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "User Not Found", "USER_ERROR_002"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("Enable Permission: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("Enable Permission: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
