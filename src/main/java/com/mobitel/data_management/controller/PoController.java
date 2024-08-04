package com.mobitel.data_management.controller;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.dto.requestDto.AddUpdatePoDto;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.service.PoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/po")
@RequiredArgsConstructor
public class PoController {
    private final PoService poService;

    @PostMapping("/addPo")
    public ResponseEntity<ApiResponse<?>> addPo(@ModelAttribute AddUpdatePoDto addUpdatePoDto){
        return poService.addPo(addUpdatePoDto);
    }

    @PutMapping("/updateMyPo")
    public ResponseEntity<ApiResponse<?>> updatePo(@RequestParam Integer id, @ModelAttribute AddUpdatePoDto addUpdatePoDto){
        return poService.updatePo(id, addUpdatePoDto);
    }

    @GetMapping("/viewPo")
    public ResponseEntity<ApiResponse<?>> viewPo(@RequestParam Integer id){
        return poService.viewPo(id);
    }

    @GetMapping("/viewAllPoList")//with sorting and pagination
    public ResponseEntity<ApiResponse<?>> viewAllPo(@RequestParam int page, @RequestParam int size,
                                                     @RequestParam String sortBy, @RequestParam boolean ascending){
        return poService.viewAllPo(page, size, sortBy, ascending);
    }

    @GetMapping("/viewAllMyPoList")//with sorting and pagination
    public ResponseEntity<ApiResponse<?>> viewAllMyPo(@RequestParam int page, @RequestParam int size,
                                                       @RequestParam String sortBy, @RequestParam boolean ascending){
        return poService.viewAllMyPo(page, size, sortBy, ascending);
    }

    @DeleteMapping("/deleteMyPo")
    public ResponseEntity<ApiResponse<?>> deletePo(@RequestParam Integer id){
        return poService.deleteMyPo(id);
    }

}
