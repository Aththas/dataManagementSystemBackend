package com.mobitel.data_management.service;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import org.springframework.http.ResponseEntity;

public interface AmcService {
    ResponseEntity<String> addAmc(AddUpdateAmcDto addUpdateAmcDto);

    ResponseEntity<?> viewAmc(Integer id);

    ResponseEntity<?> viewMyAmc(Integer id);

    ResponseEntity<?> viewAllMyAmc(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<?> viewAllAmc(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<?> deleteMyAmc(Integer id);

    ResponseEntity<String> updateAmc(Integer id, AddUpdateAmcDto addUpdateAmcDto);
}
