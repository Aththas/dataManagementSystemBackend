package com.mobitel.data_management.other.exception;

import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleObjectNotValidException(ObjectNotValidException e){
        log.error("Empty or Null Object Value is Found: "+ e.getErrorMsg().toString());
        return new ResponseEntity<>(
                new ApiResponse<>(false, null, e.getErrorMsg().toString(), "PO_ERROR_001"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleMaxSizeException(MaxUploadSizeExceededException e) {
        log.error("File size exceeds the maximum allowed limit: "+ e);
        return new ResponseEntity<>(
                new ApiResponse<>(false, null, "File size exceeds the maximum allowed limit!", "FILE_MAX_SIZE_ERROR_001"),
                HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
