package com.mobitel.data_management.service;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import org.springframework.http.ResponseEntity;

public interface AmcService {
    ResponseEntity<String> addAmc(AddUpdateAmcDto addUpdateAmcDto);

    ResponseEntity<?> viewAmc(Integer id);
}
