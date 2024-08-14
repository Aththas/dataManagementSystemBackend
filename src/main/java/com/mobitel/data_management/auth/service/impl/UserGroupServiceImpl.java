package com.mobitel.data_management.auth.service.impl;

import com.mobitel.data_management.auth.dto.requestDto.AddAccessRequestDto;
import com.mobitel.data_management.auth.dto.requestDto.UserGroupDto;
import com.mobitel.data_management.auth.dto.responseDto.ViewAccessRequestDto;
import com.mobitel.data_management.auth.dto.responseDto.ViewGroupsDto;
import com.mobitel.data_management.auth.dto.responseDto.ViewUserGroupDto;
import com.mobitel.data_management.auth.entity.user.AccessRequest;
import com.mobitel.data_management.auth.entity.user.Role;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.entity.user.UserGroup;
import com.mobitel.data_management.auth.repository.AccessRequestRepository;
import com.mobitel.data_management.auth.repository.UserGroupRepository;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.auth.service.UserGroupService;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.other.emailService.EmailService;
import com.mobitel.data_management.other.validator.ObjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupServiceImpl implements UserGroupService {
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final ObjectValidator<UserGroupDto> objectValidator;
    private final ObjectValidator<AddAccessRequestDto> accessRequestDtoObjectValidator;
    private final AccessRequestRepository accessRequestRepository;
    private final EmailService emailService;

    @Value("${spring.application.security.url}")
    private String appUrl;

    private User getCurrentUser(){
        final String userEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        return optionalUser.orElse(null);
    }
    @Override
    public ResponseEntity<ApiResponse<?>> addToGrp(UserGroupDto userGroupDto) {
        User currentUser = getCurrentUser();
        if(currentUser != null){
            objectValidator.validate(userGroupDto);
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
                            //asynchronous email sending
                            CompletableFuture.runAsync(() ->
                                    emailService.sendEmail(userToBeAdd.get().getEmail(), "Permission Enabled for Mobitel Data Management System",
                                                    "Hi!\n" +
                                                            "You have received permission to view the activities of the " + currentUser.getEmail() + " in Mobitel Data Management\n" +
                                                            "Please click the link below to visit the web app: \n" +
                                                            appUrl)
                            );


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
                            //asynchronous email sending
                            CompletableFuture.runAsync(() ->
                                    emailService.sendEmail(userToBeRemove.get().getEmail(), "Permission Disabled for Mobitel Data Management System",
                                            "Hi!\n" +
                                                    "Your permission to view the activities of the " + currentUser.getEmail() + " in Mobitel Data Management has been restricted\n" +
                                                    "Please click the link below to visit the web app: \n" +
                                                    appUrl)
                            );

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
                List<User> usersNotInGroup = userRepository.findUsersNotInIds(users.stream().map(UserGroup::getUserId).collect(Collectors.toList()));
                usersNotInGroup = usersNotInGroup.stream().filter(
                        user -> !user.getId().equals(currentUser.getId()) && !user.getRole().equals(Role.ADMIN)
                ).collect(Collectors.toList());
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

    @Override
    public ResponseEntity<ApiResponse<?>> viewMyAccessGrps(int page, int size, String sortBy, boolean ascending) {
        User currentUser = getCurrentUser();
        if(currentUser != null){
            try {
                // Create a Sort object based on the sortBy parameter and direction
                Sort sort = Sort.by(sortBy);
                sort = ascending ? sort.ascending() : sort.descending();

                // Create a Pageable object with the provided page, size, and sort
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<UserGroup> userGrpList = userGroupRepository.findByUserId(currentUser.getId(),pageable);
                List<UserGroup> userGroupListCount = userGroupRepository.findByUserId(currentUser.getId());
                int count = userGroupListCount.size();

                log.info("View Access Groups: User Data Retrieved");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, userGrpList.stream().map(this::grpList).collect(Collectors.toList()), Integer.toString(count), null),
                        HttpStatus.OK);
            } catch (Exception e){
                log.error("Access Groups: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View Access Groups: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewNonMyAccessGrps() {
        User currentUser = getCurrentUser();
        if(currentUser != null){
            try {
                List<UserGroup> users = userGroupRepository.findAllByUserId(currentUser.getId());

                List<User> usersNotInGroup = userRepository.findUsersNotInGrps(users.stream().map(UserGroup::getGrpName).collect(Collectors.toList()));
                usersNotInGroup = usersNotInGroup.stream().filter(
                        user -> !user.getId().equals(currentUser.getId())
                ).collect(Collectors.toList());
                int totalUsers = usersNotInGroup.size();

                if(usersNotInGroup.isEmpty()){
                    log.error("View Non Access Groups: Empty User List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty User List", "EMPTY_ERROR_500"),
                            HttpStatus.OK);
                }

                log.info("View Non Access Groups: User Data Retrieved");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, usersNotInGroup.stream().map(this::usersNotInGroupViewMapper).collect(Collectors.toList()), Integer.toString(totalUsers), null),
                        HttpStatus.OK);
            } catch (Exception e){
                log.error("View Non Access Groups: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View Non Access Groups: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> addAccessRequest(AddAccessRequestDto addAccessRequestDto) {
        User user = getCurrentUser();
        if(user != null){
            accessRequestDtoObjectValidator.validate(addAccessRequestDto);
            if(addAccessRequestDto != null){

                try{
                    Optional<User> optionalGrpOwner = userRepository.findById(addAccessRequestDto.getUserId());
                    if(optionalGrpOwner.isPresent()){
                        Optional<UserGroup> optionalUserGroup =
                                userGroupRepository.findByUserIdAndGrpName(user.getId(),optionalGrpOwner.get().getGrpName());
                        if(optionalUserGroup.isEmpty()){

                            Optional<AccessRequest> optionalAccessRequest =
                                    accessRequestRepository.findByRequesterIdAndGrpName(user.getId(), optionalGrpOwner.get().getGrpName());
                            if(optionalAccessRequest.isEmpty()){
                                if(!addAccessRequestDto.getReason().isEmpty()){

                                    AccessRequest accessRequest = new AccessRequest();
                                    accessRequest.setGrpOwnerId(addAccessRequestDto.getUserId());
                                    accessRequest.setReason(addAccessRequestDto.getReason());
                                    accessRequest.setStatus("PENDING");
                                    accessRequest.setGrpName(optionalGrpOwner.get().getGrpName());
                                    accessRequest.setRequesterId(user.getId());
                                    accessRequestRepository.save(accessRequest);
                                    //asynchronous email sending
                                    CompletableFuture.runAsync(() ->
                                            emailService.sendEmail(optionalGrpOwner.get().getEmail(), "Access Request in Mobitel Data Management System",
                                                    "Hi!\n" +
                                                            user.getEmail() + " have requested an access to view your activities in Mobitel Data Management\n" +
                                                            "Please click the link below to visit the web app: \n" +
                                                            appUrl)
                                    );

                                    log.info("Add Access Request: Access Request has been made, Wait till the Owner Confirmation");
                                    return new ResponseEntity<>(
                                            new ApiResponse<>(true, null, "Access Request has been made, Wait till the Owner Confirmation", null),
                                            HttpStatus.OK);

                                }else{
                                    log.error("Add Access Request: Reason for the access is required");
                                    return new ResponseEntity<>(
                                            new ApiResponse<>(false, null, "Reason for the access is required", "USER_ERROR_500"),
                                            HttpStatus.OK);
                                }
                            }else{
                                log.error("Add Access Request: Already Requested the Access");
                                return new ResponseEntity<>(
                                        new ApiResponse<>(false, null, "Already Requested the Access", "USER_ERROR_500"),
                                        HttpStatus.OK);
                            }

                        }else{
                            log.error("Add Access Request: User Already in the Group");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "User Already in the Group", "USER_ERROR_500"),
                                    HttpStatus.OK);
                        }

                    }else{
                        log.error("Add Access Request: Group Owner not found");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "User not found", "USER_ERROR_500"),
                                HttpStatus.OK);
                    }
                }catch (Exception e){
                    log.error("Add Access Request: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }else{
                log.error("Add Access Request: addAccessRequestDto object is null");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }

        }else{
            log.error("Add Access Request: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAccessRequestRequester(int page, int size, String sortBy, boolean ascending) {
        User user = getCurrentUser();
        if(user != null){
            try{
                Sort sort = Sort.by(sortBy);
                sort = ascending ? sort.ascending() : sort.descending();

                // Create a Pageable object with the provided page, size, and sort
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<AccessRequest> accessRequests = accessRequestRepository.findAllByRequesterId(user.getId(),pageable);
                List<AccessRequest> accessRequestsCount = accessRequestRepository.findAllByRequesterId(user.getId());
                int count  = accessRequestsCount.size();

                if(accessRequests.isEmpty()){
                    log.error("View Access Request as Requester: Empty User List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty User List", "EMPTY_ERROR_500"),
                            HttpStatus.OK);
                }
                log.info("View Access Request as Requester: User Data Retrieved");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, accessRequests.stream().map(this::accessRequestViewMapper).collect(Collectors.toList()), Integer.toString(count), null),
                        HttpStatus.OK);

            }catch (Exception e){
                log.error("View Access Request as Requester: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }else{
            log.error("View Access Request as Requester: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAccessRequestGrpOwner(int page, int size, String sortBy, boolean ascending) {
        User user = getCurrentUser();
        if(user != null){
            try{
                Sort sort = Sort.by(sortBy);
                sort = ascending ? sort.ascending() : sort.descending();

                // Create a Pageable object with the provided page, size, and sort
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<AccessRequest> accessRequests = accessRequestRepository.findAllByGrpOwnerId(user.getId(),pageable);
                List<AccessRequest> accessRequestsCount = accessRequestRepository.findAllByGrpOwnerId(user.getId());
                int count  = accessRequestsCount.size();

                if(accessRequests.isEmpty()){
                    log.error("View Access Request as Group Owner: Empty User List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty User List", "EMPTY_ERROR_500"),
                            HttpStatus.OK);
                }
                log.info("View Access Request as Group Owner: User Data Retrieved");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, accessRequests.stream().map(this::accessRequestViewMapper).collect(Collectors.toList()), Integer.toString(count), null),
                        HttpStatus.OK);

            }catch (Exception e){
                log.error("View Access Request as Group Owner: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }else{
            log.error("View Access Request as Group Owner: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> acceptAccessRequest(Integer id) {
        User user = getCurrentUser();
        if(user!= null){
            if(id != null){
                try{
                    Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(id);
                    if(optionalAccessRequest.isPresent() && user.getId().equals(optionalAccessRequest.get().getGrpOwnerId())){
                        AccessRequest accessRequest = optionalAccessRequest.get();

                        UserGroup userGroup = new UserGroup();
                        userGroup.setGrpName(accessRequest.getGrpName());
                        userGroup.setUserId(accessRequest.getRequesterId());
                        userGroupRepository.save(userGroup);

                        User reqUser = userRepository.findById(accessRequest.getRequesterId()).orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND"));
                        //asynchronous email sending
                        CompletableFuture.runAsync(() ->
                                emailService.sendEmail(reqUser.getEmail(), "Access Request Update in Mobitel Data Management System",
                                        "Hi!\n" +
                                                "Your access request to view the activities of " + user.getEmail() + " has been accepted in Mobitel Data Management\n" +
                                                "Please click the link below to visit the web app: \n" +
                                                appUrl)
                        );

                        accessRequestRepository.deleteById(id);

                        log.info("Accept Access Request: Accepted");
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, null, "Access Request Accepted", null),
                                HttpStatus.OK);

                    }else{
                        log.error("Accept Access Request: Request Not Found");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Request Not Found", "ACCEPT_ERROR_500"),
                                HttpStatus.OK);
                    }
                }catch(Exception e){
                    log.error("Accept Access Request: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }else{
                log.error("Accept Access Request: Null Access Request ID");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Access Request ID", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }

        }else{
            log.error("Accept Access Request: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> rejectAccessRequest(Integer id) {
        User user = getCurrentUser();
        if(user!= null){
            if(id != null){
                try{
                    Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(id);
                    if(optionalAccessRequest.isPresent() && user.getId().equals(optionalAccessRequest.get().getGrpOwnerId())){
                        AccessRequest accessRequest = optionalAccessRequest.get();

                        User reqUser = userRepository.findById(accessRequest.getRequesterId()).orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND"));
                        //asynchronous email sending
                        CompletableFuture.runAsync(() ->
                                emailService.sendEmail(reqUser.getEmail(), "Access Request Update in Mobitel Data Management System",
                                        "Hi!\n" +
                                                "Your access request to view the activities of " + user.getEmail() + " has been rejected in Mobitel Data Management\n" +
                                                "Please click the link below to visit the web app: \n" +
                                                appUrl)
                        );

                        accessRequestRepository.deleteById(id);

                        log.info("Reject Access Request: Rejected");
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, null, "Access Request Rejected", null),
                                HttpStatus.OK);

                    }else{
                        log.error("Reject Access Request: Request Not Found");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Request Not Found", "ACCEPT_ERROR_500"),
                                HttpStatus.OK);
                    }
                }catch(Exception e){
                    log.error("Reject Access Request: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }else{
                log.error("Reject Access Request: Null Access Request ID");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Access Request ID", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }

        }else{
            log.error("Reject Access Request: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }


    private ViewAccessRequestDto accessRequestViewMapper(AccessRequest accessRequest) {
        ViewAccessRequestDto viewAccessRequestDto = new ViewAccessRequestDto();
        viewAccessRequestDto.setId(accessRequest.getId());

        Optional<User> optionalUserRequester = userRepository.findById(accessRequest.getRequesterId());
        if(optionalUserRequester.isPresent()){
            User requestUser = optionalUserRequester.get();
            viewAccessRequestDto.setRequesterEmail(requestUser.getEmail());
        }

        viewAccessRequestDto.setRequesterId(accessRequest.getRequesterId());
        viewAccessRequestDto.setStatus(accessRequest.getStatus());
        viewAccessRequestDto.setReason(accessRequest.getReason());
        viewAccessRequestDto.setGrpOwnerId(accessRequest.getGrpOwnerId());

        Optional<User> optionalUser = userRepository.findById(accessRequest.getGrpOwnerId());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            viewAccessRequestDto.setGrpName(user.getEmail());
        }
        return viewAccessRequestDto;
    }

    private ViewGroupsDto grpList(UserGroup userGroup) {
        ViewGroupsDto viewGroupsDto = new ViewGroupsDto();
        Optional<User> optionalUser = userRepository.findByGrpName(userGroup.getGrpName());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            viewGroupsDto.setId(userGroup.getId());
            viewGroupsDto.setUserGroups(user.getEmail());
        }
        return viewGroupsDto;
    }

    private ViewUserGroupDto usersNotInGroupViewMapper(User user) {
        ViewUserGroupDto viewUserGroupDto = new ViewUserGroupDto();
        viewUserGroupDto.setUserId(user.getId());
        viewUserGroupDto.setEmail(user.getEmail());
        return viewUserGroupDto;
    }

    private ViewUserGroupDto userViewMapper(UserGroup userGroup) {
        ViewUserGroupDto viewUserGroupDto = new ViewUserGroupDto();
        Optional<User> optionalUser = userRepository.findById(userGroup.getUserId());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            viewUserGroupDto.setEmail(user.getEmail());
            viewUserGroupDto.setUserId(userGroup.getUserId());
        }
        return viewUserGroupDto;
    }


}
