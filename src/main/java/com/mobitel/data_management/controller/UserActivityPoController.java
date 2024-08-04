package com.mobitel.data_management.controller;

import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.service.UserActivityAmcService;
import com.mobitel.data_management.service.UserActivityPoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/userActivityPo")
@RequiredArgsConstructor
public class UserActivityPoController {
    private final UserActivityPoService userActivityPoService;

    @GetMapping("/viewAllActivities")
    public ResponseEntity<ApiResponse<?>> viewAllActivities(@RequestParam int page, @RequestParam int size,
                                                            @RequestParam String sortBy, @RequestParam boolean ascending){
        return userActivityPoService.viewAllActivities(page, size, sortBy, ascending);
    }

    @GetMapping("/viewAllMyActivities")
    public ResponseEntity<ApiResponse<?>> viewAllMyActivities(@RequestParam int page, @RequestParam int size,
                                                              @RequestParam String sortBy, @RequestParam boolean ascending){
        return userActivityPoService.viewAllMyActivities(page, size, sortBy, ascending);
    }

    @GetMapping("/viewActivity")
    public ResponseEntity<ApiResponse<?>> viewActivity(@RequestParam Integer id){
        return userActivityPoService.viewActivity(id);
    }
}
