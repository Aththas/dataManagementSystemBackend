package com.mobitel.data_management.service;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.dto.requestDto.AddUpdatePoDto;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface PoService {
    ResponseEntity<ApiResponse<?>> addPo(AddUpdatePoDto addUpdatePoDto);

    ResponseEntity<ApiResponse<?>> updatePo(Integer id, AddUpdatePoDto addUpdatePoDto);

    ResponseEntity<ApiResponse<?>> viewPo(Integer id);

    ResponseEntity<ApiResponse<?>> viewAllPo(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<ApiResponse<?>> viewAllMyPo(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<ApiResponse<?>> deleteMyPo(Integer id);
}
