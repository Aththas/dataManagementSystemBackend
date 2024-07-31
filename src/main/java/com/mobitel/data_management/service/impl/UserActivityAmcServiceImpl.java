package com.mobitel.data_management.service.impl;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.entity.UserActivityAmc;
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

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivityAmcServiceImpl implements UserActivityAmcService {
    private final UserActivityAmcRepository userActivityAmcRepository;
    private final UserActivityAmcMapper userActivityAmcMapper;
    private final UserRepository userRepository;

    private User getCurrentUser(){
        final String userEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        return optionalUser.orElse(null);
    }
    @Override
    public void saveUserActivity(User user, String action, String filePathBeforeUpdate, String filePathAfterUpdate,
                                 String rowBefore, String rowAfter, Integer currentVersion,String description) {
        UserActivityAmc userActivityAmc = new UserActivityAmc();
        userActivityAmc.setVersion("version " + currentVersion);
        userActivityAmc.setAction(action);
        userActivityAmc.setDescription(description);
        userActivityAmc.setUser(user);
        userActivityAmc.setBeforeFile(filePathBeforeUpdate);
        userActivityAmc.setAfterFile(filePathAfterUpdate);
        userActivityAmc.setRowBefore(rowBefore);
        userActivityAmc.setRowAfter(rowAfter);
        userActivityAmcRepository.save(userActivityAmc);
    }

    @Override
    public ResponseEntity<?> viewAllActivities(int page, int size, String sortBy, boolean ascending) {
        try{
            // Create a Sort object based on the sortBy parameter and direction
            Sort sort = Sort.by(sortBy);
            sort = ascending ? sort.ascending() : sort.descending();

            // Create a Pageable object with the provided page, size, and sort
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<UserActivityAmc> userActivityAmcList = userActivityAmcRepository.findAll(pageable);

            if(userActivityAmcList.isEmpty()){
                log.error("View All Activities: Empty List");
                return new ResponseEntity<>("Empty List",HttpStatus.OK);
            }
            log.info("View All Activities: Listed All Activities List");
            return new ResponseEntity<>(userActivityAmcList.stream().map(userActivityAmcMapper::userActivityViewMapper).collect(Collectors.toList()), HttpStatus.OK);


        }catch (Exception e){
            log.error("View All Activities: " + e);
            return new ResponseEntity<>("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> viewAllMyActivities(int page, int size, String sortBy, boolean ascending) {
        User user = getCurrentUser();
        if(user != null){
            // Create a Sort object based on the sortBy parameter and direction
            Sort sort = Sort.by(sortBy);
            sort = ascending ? sort.ascending() : sort.descending();

            // Create a Pageable object with the provided page, size, and sort
            Pageable pageable = PageRequest.of(page, size, sort);

            // Retrieve the paginated and sorted results
            Page<UserActivityAmc> userActivityAmcList = userActivityAmcRepository.findAllByUserId(user.getId(),pageable);

            if(userActivityAmcList.isEmpty()){
                log.error("View All My Activities: Empty List");
                return new ResponseEntity<>("Empty List",HttpStatus.OK);
            }
            log.info("View All My Activities: Listed All My Activities List");
            return new ResponseEntity<>(userActivityAmcList.stream().map(userActivityAmcMapper::userActivityViewMapper).collect(Collectors.toList()), HttpStatus.OK);

        }else{
            log.error("View All My Activities: Unauthorized Access");
            return new ResponseEntity<>("Unauthorized Access", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<?> viewActivity(Integer id) {
        if(id != null){
            try{
                Optional<UserActivityAmc> optionalUserActivityAmc = userActivityAmcRepository.findById(id);
                if(optionalUserActivityAmc.isPresent()){
                    UserActivityAmc userActivityAmc = optionalUserActivityAmc.get();
                    log.info("View Activity: User Activity Data Retrieved - " + userActivityAmc.getVersion());
                    return new ResponseEntity<>(userActivityAmcMapper.userSingleActivityViewMapper(userActivityAmc),HttpStatus.OK);
                }
                log.error("View Activity: User Activity Not Found");
                return new ResponseEntity<>("User Activity Not Found",HttpStatus.OK);
            }catch (Exception e){
                log.error("Activity AMC: " + e);
                return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View Activity: Null User ID");
            return new ResponseEntity<>("Null User ID",HttpStatus.BAD_REQUEST);
        }
    }
}
