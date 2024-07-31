package com.mobitel.data_management.service;

import com.mobitel.data_management.auth.entity.user.User;
import org.springframework.http.ResponseEntity;

public interface UserActivityAmcService {
    void saveUserActivity(User user, String action, String filePathBeforeUpdate, String filePathAfterUpdate, String rowBefore, String rowAfter, Integer currentVersion);

    ResponseEntity<?> viewAllActivities(int page, int size, String sortBy, boolean ascending);

    Integer findLastId();

    ResponseEntity<?> viewAllMyActivities(int page, int size, String sortBy, boolean ascending);

    ResponseEntity<?> viewActivity(Integer id);
}
