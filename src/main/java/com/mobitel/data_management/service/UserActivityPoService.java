package com.mobitel.data_management.service;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserActivityPoService {
    Integer findLastId();

    void saveUserActivity(User user, String action, String filePathBeforeUpdate, String filePathAfterUpdate, String rowBefore, String rowAfter, Integer currentVersion, String description);

    ResponseEntity<ApiResponse<?>> viewActivity(Integer id);

    ResponseEntity<ApiResponse<?>> viewAllMyActivities(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<ApiResponse<?>> viewAllActivities(int page, int size, String sortBy, boolean ascending);
}
