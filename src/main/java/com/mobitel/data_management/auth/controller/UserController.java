package com.mobitel.data_management.auth.controller;

import com.mobitel.data_management.auth.dto.requestDto.AddUserDto;
import com.mobitel.data_management.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody AddUserDto addUserDto){
        return userService.addUser(addUserDto);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestParam Integer id, @RequestBody AddUserDto addUserDto){
        return userService.updateUser(id,addUserDto);
    }

    @GetMapping("/viewUser")
    public ResponseEntity<?> viewUser(@RequestParam Integer id){
        return userService.viewUser(id);
    }

    @GetMapping("/viewUsers")
    public ResponseEntity<?> viewUsers(){
        return userService.viewUsers();
    }
}
