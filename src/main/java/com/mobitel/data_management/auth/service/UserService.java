package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<String> addUser(AddUserDto addUserDto);

    ResponseEntity<String> updateUser(Integer id, AddUserDto addUserDto);

    ResponseEntity<?> viewUser(Integer id);

    ResponseEntity<?> viewUsers(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<String> disableUser(Integer id);

    ResponseEntity<String> enableUser(Integer id);
}
