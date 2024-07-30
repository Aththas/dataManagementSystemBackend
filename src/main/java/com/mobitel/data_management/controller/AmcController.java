package com.mobitel.data_management.controller;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.service.AmcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/amc")
@RequiredArgsConstructor
public class AmcController {
    private final AmcService amcService;

    @PostMapping("/addAmc")
    public ResponseEntity<String> addAmc(@RequestBody AddUpdateAmcDto addUpdateAmcDto){
        return amcService.addAmc(addUpdateAmcDto);
    }

    @GetMapping("/viewAmc")
    public ResponseEntity<?> viewAmc(@RequestParam Integer id){
        return amcService.viewAmc(id);
    }
}
