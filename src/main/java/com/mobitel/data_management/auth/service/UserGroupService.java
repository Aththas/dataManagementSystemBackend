package com.mobitel.data_management.auth.service;

import com.mobitel.data_management.auth.dto.requestDto.UserGroupDto;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserGroupService {
    ResponseEntity<ApiResponse<?>> addToGrp(UserGroupDto userGroupDto);

    ResponseEntity<ApiResponse<?>> removeFromGrp(Integer id);

    ResponseEntity<ApiResponse<?>> getAllMyGrpUsers(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<ApiResponse<?>> getAllNonMyGrpUsers();
}
