package com.mobitel.data_management.auth.controller;

import com.mobitel.data_management.auth.dto.requestDto.UserGroupDto;
import com.mobitel.data_management.auth.service.UserGroupService;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/grp/")
@RequiredArgsConstructor
@Slf4j
public class UserGroupController {
    private final UserGroupService userGroupService;

    @PostMapping("/addToGrp")
    public ResponseEntity<ApiResponse<?>> addToGrp(@RequestBody UserGroupDto userGroupDto){
        return userGroupService.addToGrp(userGroupDto);
    }

    @DeleteMapping("removeFromGrp")
    public ResponseEntity<ApiResponse<?>> removeFromGrp(@RequestParam Integer id){
        return userGroupService.removeFromGrp(id);
    }

    @GetMapping("/getAllMyGrpUsers")
    public ResponseEntity<ApiResponse<?>> getAllMyGrpUsers(@RequestParam int page, @RequestParam int size,
                                                           @RequestParam String sortBy, @RequestParam boolean ascending){
        return  userGroupService.getAllMyGrpUsers(page, size, sortBy, ascending);
    }

    @GetMapping("/getAllNonMyGrpUsers")
    public ResponseEntity<ApiResponse<?>> getAllNonMyGrpUsers(){
        return  userGroupService.getAllNonMyGrpUsers();
    }
}
