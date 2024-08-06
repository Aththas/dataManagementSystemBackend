package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.UserGroupDto;
import com.mobitel.data_management.auth.dto.responseDto.ViewUserGroupDto;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.entity.user.UserGroup;
import com.mobitel.data_management.auth.repository.UserGroupRepository;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.UserGroupService;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupServiceImpl implements UserGroupService {
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    private User getCurrentUser(){
        final String userEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        return optionalUser.orElse(null);
    }
    @Override
    public ResponseEntity<ApiResponse<?>> addToGrp(UserGroupDto userGroupDto) {
        User currentUser = getCurrentUser();
        if(currentUser != null){
            if(userGroupDto != null){
                try{
                    Optional<User> userToBeAdd = userRepository.findById(userGroupDto.getUserId());
                    if(userToBeAdd.isPresent()){
                        Optional<UserGroup> optionalUserGroup =
                                userGroupRepository.findByUserIdAndGrpName(userGroupDto.getUserId(), currentUser.getGrpName());
                        if(optionalUserGroup.isEmpty()){
                            UserGroup userGroup = new UserGroup();
                            userGroup.setGrpName(currentUser.getGrpName());
                            userGroup.setUserId(userGroupDto.getUserId());
                            userGroupRepository.save(userGroup);

                            log.info("User Add to Group: User Added to My View group");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(true, null, "User Added Successfully to My View Group", null),
                                    HttpStatus.OK);
                        }else{
                            log.error("User Add to Group: User Already in the Group");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "User Already in the Group", "USER_ERROR_500"),
                                    HttpStatus.OK);
                        }


                    }else{
                        log.error("User Add to Group: User not found");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "User not found", "USER_ERROR_500"),
                                HttpStatus.OK);
                    }

                }catch (Exception e){
                    log.error("User Add to Group: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("User Add to Group: userGroupDto object is null");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }

        }else{
            log.error("User Add to Group: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> removeFromGrp(Integer id) {
        User currentUser = getCurrentUser();
        if(currentUser != null){
            if(id != null){
                try{
                    Optional<User> userToBeRemove = userRepository.findById(id);
                    if(userToBeRemove.isPresent()){
                        Optional<UserGroup> optionalUserGroup =
                                userGroupRepository.findByUserIdAndGrpName(id, currentUser.getGrpName());
                        if(optionalUserGroup.isPresent()){
                            userGroupRepository.deleteByUserIdAndGrpName(id, currentUser.getGrpName());

                            log.info("User Remove from Group: User Removed from My View group");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(true, null, "User Removed Successfully from My View Group", null),
                                    HttpStatus.OK);
                        }else{
                            log.error("User Remove from Group:  User not in the group");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "User not in the group", "USER_ERROR_500"),
                                    HttpStatus.OK);
                        }

                    }else{
                        log.error("User Remove from Group: User not found");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "User not found", "USER_ERROR_500"),
                                HttpStatus.OK);
                    }

                }catch (Exception e){
                    log.error("User Remove from Group: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("User Remove from Group: userGroupDto object is null");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }

        }else{
            log.error("User Remove from Group: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllMyGrpUsers(int page, int size, String sortBy, boolean ascending) {
        User currentUser = getCurrentUser();
        if(currentUser != null){
            try {
                // Create a Sort object based on the sortBy parameter and direction
                Sort sort = Sort.by(sortBy);
                sort = ascending ? sort.ascending() : sort.descending();

                // Create a Pageable object with the provided page, size, and sort
                Pageable pageable = PageRequest.of(page, size, sort);

                // Retrieve the paginated and sorted results
                Page<UserGroup> usersInGroup = userGroupRepository.findAllByGrpName(currentUser.getGrpName(), pageable);
                List<UserGroup> countUsers = userGroupRepository.findAllByGrpName(currentUser.getGrpName());
                int totalUsers = countUsers.size();
                if(usersInGroup.isEmpty()){
                    log.error("View Users: Empty User List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty User List", "EMPTY_ERROR_500"),
                            HttpStatus.OK);
                }
                log.info("View Users: User Data Retrieved");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, usersInGroup.stream().map(this::userViewMapper).collect(Collectors.toList()), Integer.toString(totalUsers), null),
                        HttpStatus.OK);
            } catch (Exception e){
                log.error("View Users: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View Users: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllNonMyGrpUsers() {
        User currentUser = getCurrentUser();
        if(currentUser != null){
            try {
                List<UserGroup> users = userGroupRepository.findAllByGrpName(currentUser.getGrpName());
                List<User> usersNotInGroup = userRepository.findUsersNotInIds(users.stream().map(UserGroup::getId).collect(Collectors.toList()));
                int totalUsers = usersNotInGroup.size();
                if(usersNotInGroup.isEmpty()){
                    log.error("View Users: Empty User List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty User List", "EMPTY_ERROR_500"),
                            HttpStatus.OK);
                }
                log.info("View Users: User Data Retrieved");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, usersNotInGroup.stream().map(this::usersNotInGroupViewMapper).collect(Collectors.toList()), Integer.toString(totalUsers), null),
                        HttpStatus.OK);
            } catch (Exception e){
                log.error("View Users: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View Users: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    private ViewUserGroupDto usersNotInGroupViewMapper(User user) {
        ViewUserGroupDto viewUserGroupDto = new ViewUserGroupDto();
        viewUserGroupDto.setEmail(user.getEmail());
        return viewUserGroupDto;
    }

    private ViewUserGroupDto userViewMapper(UserGroup userGroup) {
        ViewUserGroupDto viewUserGroupDto = new ViewUserGroupDto();
        Optional<User> optionalUser = userRepository.findById(userGroup.getUserId());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            viewUserGroupDto.setEmail(user.getEmail());
        }
        return viewUserGroupDto;
    }


}
