package com.mobitel.data_management.mapper;

import com.mobitel.data_management.auth.dto.responseDto.ViewUserDto;
import com.mobitel.data_management.auth.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public ViewUserDto userViewMapper(User user){
        ViewUserDto userDto = new ViewUserDto();
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setEmail(user.getEmail());
        userDto.setRole(String.valueOf(user.getRole()));
        return userDto;
    }
}
