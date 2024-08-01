package com.mobitel.data_management.other.apiResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;
}
