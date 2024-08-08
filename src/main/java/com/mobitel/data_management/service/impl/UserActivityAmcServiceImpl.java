package com.mobitel.data_management.service.impl;

import com.mobitel.data_management.auth.entity.user.Role;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.entity.user.UserGroup;
import com.mobitel.data_management.auth.repository.UserGroupRepository;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.entity.Po;
import com.mobitel.data_management.entity.UserActivityAmc;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.other.mapper.UserActivityAmcMapper;
import com.mobitel.data_management.repository.UserActivityAmcRepository;
import com.mobitel.data_management.service.AmcService;
import com.mobitel.data_management.service.UserActivityAmcService;
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
public class UserActivityAmcServiceImpl implements UserActivityAmcService {
    private final UserActivityAmcRepository userActivityAmcRepository;
    private final UserActivityAmcMapper userActivityAmcMapper;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    private User getCurrentUser(){
        final String userEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        return optionalUser.orElse(null);
    }
    @Override
    public void saveUserActivity(User user, String action, String filePathBeforeUpdate, String filePathAfterUpdate,
                                 String rowBefore, String rowAfter, Integer currentVersion,String description) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = now.format(formatter);

        UserActivityAmc userActivityAmc = new UserActivityAmc();
        userActivityAmc.setVersion("version " + currentVersion);
        userActivityAmc.setAction(action);
        userActivityAmc.setDescription(description);
        userActivityAmc.setUser(user);
        userActivityAmc.setBeforeFile(filePathBeforeUpdate);
        userActivityAmc.setAfterFile(filePathAfterUpdate);
        userActivityAmc.setRowBefore(rowBefore);
        userActivityAmc.setRowAfter(rowAfter);
        userActivityAmc.setDateTime(formattedDate);
        userActivityAmcRepository.save(userActivityAmc);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAllActivities(int page, int size, String sortBy, boolean ascending) {
        User user = getCurrentUser();
        if(user != null){
            try{
                // Create a Sort object based on the sortBy parameter and direction
                Sort sort = Sort.by(sortBy);
                sort = ascending ? sort.ascending() : sort.descending();

                // Create a Pageable object with the provided page, size, and sort
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<UserActivityAmc> userActivityAmcList = null;
                List<UserActivityAmc> userActivityAmcListCount = null;
                int count = 0;
                if(user.getRole().equals(Role.ADMIN)){
                    userActivityAmcList = userActivityAmcRepository.findAll(pageable);
                    userActivityAmcListCount = userActivityAmcRepository.findAll();
                }else{
                    List<String> grpNames = userGroupRepository.findGroupNamesByUserId(user.getId());
                    List<User> users = userRepository.findAllByGroupNames(grpNames);
                    users.add(user);
                    userActivityAmcList = userActivityAmcRepository.findAllByUser(users,pageable);
                    userActivityAmcListCount = userActivityAmcRepository.findAllByUser(users);
                }


                count = userActivityAmcListCount.size();
                log.info("count "+ count );
                if(userActivityAmcList.isEmpty()){
                    log.error("View All Activities: Empty List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty List", "EMPTY_ERROR_001"),
                            HttpStatus.OK);
                }
                log.info("View All Activities: Listed All Activities List");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, userActivityAmcList.stream().map(userActivityAmcMapper::userActivityViewMapper).collect(Collectors.toList()), Integer.toString(count), null),
                        HttpStatus.OK);

            }catch (Exception e){
                log.error("View All Activities: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View All Activities: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Invalid Authentication", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public Integer findLastId() {
        Integer lastId = userActivityAmcRepository.findLastId();
        if(lastId == null){
            lastId = 0;
        }
        return lastId;
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
                Page<UserActivityAmc> userActivityAmcList = userActivityAmcRepository.findAllByUserId(user.getId(),pageable);
                List<UserActivityAmc> userActivityAmcListCount = userActivityAmcRepository.findAllByUserId(user.getId());
                int count = userActivityAmcListCount.size();
                if(userActivityAmcList.isEmpty()){
                    log.error("View All My Activities: Empty List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty List", "EMPTY_ERROR_001"),
                            HttpStatus.OK);
                }
                log.info("View All My Activities: Listed All My Activities List");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, userActivityAmcList.stream().map(userActivityAmcMapper::userActivityViewMapper).collect(Collectors.toList()), Integer.toString(count), null),
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
    public ResponseEntity<ApiResponse<?>> viewActivity(Integer id) {
        User user = getCurrentUser();
        if(user != null){
            if(id != null){
                try{
                    Optional<UserActivityAmc> optionalUserActivityAmc = userActivityAmcRepository.findById(id);
                    if(optionalUserActivityAmc.isPresent()){
                        UserActivityAmc userActivityAmc = optionalUserActivityAmc.get();

                        String amcOwnerGrp = userActivityAmc.getUser().getGrpName();
                        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByUserIdAndGrpName(user.getId(), amcOwnerGrp);
                        if(userActivityAmc.getUser().equals(user) || user.getRole().equals(Role.ADMIN) || optionalUserGroup.isPresent())
                        {
                            log.info("View Activity: User Activity Data Retrieved - " + userActivityAmc.getVersion());
                            return new ResponseEntity<>(
                                    new ApiResponse<>(true, userActivityAmcMapper.userSingleActivityViewMapper(userActivityAmc), "User Activity Data Retrieved", null),
                                    HttpStatus.OK);
                        }else{
                            log.error("View Activity: Restricted View Access");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "Restricted View Access", "AMC_ERROR_002"),
                                    HttpStatus.OK);
                        }

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
        }else{
            log.error("View Activity: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Invalid Authentication", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }
}
