package com.mobitel.data_management.other.mapper;

import com.mobitel.data_management.auth.dto.responseDto.ViewUserDto;
import com.mobitel.data_management.auth.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public ViewUserDto userViewMapper(User user){
        ViewUserDto userDto = new ViewUserDto();
        userDto.setId(user.getId());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setEmail(user.getEmail());
        userDto.setRole(String.valueOf(user.getRole()));
        userDto.setViewPermission(user.isViewPermission());
        userDto.setSuperUser(user.isSuperUser());
        return userDto;
    }
}
