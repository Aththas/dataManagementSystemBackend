package com.mobitel.data_management.service;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AmcService {
    ResponseEntity<ApiResponse<?>> addAmc(AddUpdateAmcDto addUpdateAmcDto);

    ResponseEntity<ApiResponse<?>> viewAmc(Integer id);

    ResponseEntity<ApiResponse<?>> viewMyAmc(Integer id);

    ResponseEntity<ApiResponse<?>> viewAllMyAmc(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<ApiResponse<?>> viewAllAmc(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<ApiResponse<?>> deleteMyAmc(Integer id);

    ResponseEntity<ApiResponse<?>> updateAmc(Integer id, AddUpdateAmcDto addUpdateAmcDto);

    ResponseEntity<ApiResponse<?>> acknowledge(Integer id);
}
