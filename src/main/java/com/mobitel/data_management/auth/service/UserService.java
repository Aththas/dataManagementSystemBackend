package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<ApiResponse<?>> addUser(AddUserDto addUserDto);

    ResponseEntity<ApiResponse<?>> updateUser(Integer id, AddUserDto addUserDto);

    ResponseEntity<ApiResponse<?>> viewUser(Integer id);

    ResponseEntity<ApiResponse<?>> viewUsers(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<ApiResponse<?>> disableUser(Integer id);

    ResponseEntity<ApiResponse<?>> enableUser(Integer id);

    ResponseEntity<ApiResponse<?>> enableCsvPermission(Integer id);

    ResponseEntity<ApiResponse<?>> disableCsvPermission(Integer id);
}
