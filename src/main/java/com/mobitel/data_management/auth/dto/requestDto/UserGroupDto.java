package com.mobitel.data_management.auth.dto.requestDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserGroupDto {
    @Positive(message = "User Id must be a positive number")
    @NotNull(message = "Invalid User Id")
    private Integer userId;
}
