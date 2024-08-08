package com.mobitel.data_management.auth.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddAccessRequestDto {
    @NotEmpty(message = "Empty Reason")
    @NotNull(message = "Invalid Reason")
    private String reason;

    @NotNull(message = "Group Owner Id cannot be null")
    @Positive(message = "Group Owner Id must be a positive number")
    private Integer userId;//grpOwnerID
}
