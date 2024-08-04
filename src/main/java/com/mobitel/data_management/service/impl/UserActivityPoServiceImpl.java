package com.mobitel.data_management.service.impl;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.entity.UserActivityAmc;
import com.mobitel.data_management.entity.UserActivityPo;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.other.mapper.UserActivityPoMapper;
import com.mobitel.data_management.repository.UserActivityPoRepository;
import com.mobitel.data_management.service.UserActivityPoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivityPoServiceImpl implements UserActivityPoService {
    private final UserActivityPoRepository userActivityPoRepository;
    private final UserActivityPoMapper userActivityPoMapper;
    private final UserRepository userRepository;

    private User getCurrentUser(){
        final String userEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        return optionalUser.orElse(null);
    }

    @Override
    public Integer findLastId() {
        return userActivityPoRepository.findLastId();
    }

    @Override
    public void saveUserActivity(User user, String action, String filePathBeforeUpdate, String filePathAfterUpdate, String rowBefore, String rowAfter, Integer currentVersion, String description) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = now.format(formatter);

        UserActivityPo userActivityPo = new UserActivityPo();
        userActivityPo.setVersion("version " + currentVersion);
        userActivityPo.setAction(action);
        userActivityPo.setDescription(description);
        userActivityPo.setUser(user);
        userActivityPo.setBeforeFile(filePathBeforeUpdate);
        userActivityPo.setAfterFile(filePathAfterUpdate);
        userActivityPo.setRowBefore(rowBefore);
        userActivityPo.setRowAfter(rowAfter);
        userActivityPo.setDateTime(formattedDate);
        userActivityPoRepository.save(userActivityPo);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewActivity(Integer id) {
        if(id != null){
            try{
                Optional<UserActivityPo> optionalUserActivityAmc = userActivityPoRepository.findById(id);
                if(optionalUserActivityAmc.isPresent()){
                    UserActivityPo userActivityPo = optionalUserActivityAmc.get();
                    log.info("View Activity: User Activity Data Retrieved - " + userActivityPo.getVersion());
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, userActivityPoMapper.userSingleActivityViewMapper(userActivityPo), "User Activity Data Retrieved", null),
                            HttpStatus.OK);
                }
                log.error("View Activity: User Activity Not Found");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "User Activity Not Found", "EMPTY_ERROR_001"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("Activity Activity: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View Activity: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAllMyActivities(int page, int size, String sortBy, boolean ascending) {
        User user = getCurrentUser();
        if(user != null){
            try{
                // Create a Sort object based on the sortBy parameter and direction
                Sort sort = Sort.by(sortBy);
                sort = ascending ? sort.ascending() : sort.descending();

                // Create a Pageable object with the provided page, size, and sort
                Pageable pageable = PageRequest.of(page, size, sort);

                // Retrieve the paginated and sorted results
                Page<UserActivityPo> userActivityPoList = userActivityPoRepository.findAllByUserId(user.getId(),pageable);
                List<UserActivityPo> userActivityPoListCount = userActivityPoRepository.findAllByUserId(user.getId());
                int count = userActivityPoListCount.size();
                if(userActivityPoList.isEmpty()){
                    log.error("View All My Activities: Empty List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty List", "EMPTY_ERROR_001"),
                            HttpStatus.OK);
                }
                log.info("View All My Activities: Listed All My Activities List");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, userActivityPoList.stream().map(userActivityPoMapper::userActivityViewMapper).collect(Collectors.toList()), Integer.toString(count), null),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("View All My Activities: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View All My Activities: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Invalid Authentication", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAllActivities(int page, int size, String sortBy, boolean ascending) {
        try{
            // Create a Sort object based on the sortBy parameter and direction
            Sort sort = Sort.by(sortBy);
            sort = ascending ? sort.ascending() : sort.descending();

            // Create a Pageable object with the provided page, size, and sort
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<UserActivityPo> userActivityPoList = userActivityPoRepository.findAll(pageable);
            List<UserActivityPo> userActivityPoListCount = userActivityPoRepository.findAll();
            int count = userActivityPoListCount.size();
            if(userActivityPoList.isEmpty()){
                log.error("View All Activities: Empty List");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Empty List", "EMPTY_ERROR_001"),
                        HttpStatus.OK);
            }
            log.info("View All Activities: Listed All Activities List");
            return new ResponseEntity<>(
                    new ApiResponse<>(true, userActivityPoList.stream().map(userActivityPoMapper::userActivityViewMapper).collect(Collectors.toList()), Integer.toString(count), null),
                    HttpStatus.OK);

        }catch (Exception e){
            log.error("View All Activities: " + e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
