package com.mobitel.data_management.service.impl;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.other.csvService.AmcCsvConverter;
import com.mobitel.data_management.other.dateUtility.DateFormatConverter;
import com.mobitel.data_management.other.mapper.AmcMapper;
import com.mobitel.data_management.other.stringUtility.StringUtils;
import com.mobitel.data_management.other.validator.ObjectValidator;
import com.mobitel.data_management.repository.AmcRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmcServiceImpl implements AmcService {
    private final UserRepository userRepository;
    private final ObjectValidator<AddUpdateAmcDto> addAmcObjectValidator;
    private final AmcRepository amcRepository;
    private final AmcMapper amcMapper;
    private final AmcCsvConverter amcCsvConverter;
    private final UserActivityAmcService userActivityAmcService;
    private final StringUtils stringUtils;
    private final DateFormatConverter dateFormatConverter;

    private User getCurrentUser(){
        final String userEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        return optionalUser.orElse(null);
    }
    @Override
    public ResponseEntity<ApiResponse<?>> addAmc(AddUpdateAmcDto addUpdateAmcDto) {
        User user = getCurrentUser();
        if(user != null){
            addAmcObjectValidator.validate(addUpdateAmcDto);
            if(addUpdateAmcDto != null){
                try{
                    Optional<Amc> optionalAmc = amcRepository.findByContractName(addUpdateAmcDto.getContractName());
                    if(optionalAmc.isPresent()){
                        log.error("AMC Contract Add: Contract Name Already Exist");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Contract Name Already Exist", "AMC_ERROR_001"),
                                HttpStatus.OK);
                    }

                    String action = "add";
                    String description = user.getEmail() + " Added a Row";
                    Integer currentVersion = userActivityAmcService.findLastId()+1;

                    String beforeName= "before version " + currentVersion;
                    String filePathBeforeUpdate = amcCsvConverter.generateCsvForAmc(beforeName);
                    String rowBefore = "";

                    Amc amc = new Amc();
                    amc.setUser(user);
                    amcRepository.save(amcMapper.addUpdateAmcMapper(amc,addUpdateAmcDto));

                    String afterName= "after version " + currentVersion;
                    String filePathAfterUpdate = amcCsvConverter.generateCsvForAmc(afterName);
                    String rowAfter = amc.getUserDivision() + " | " + amc.getContractName() + " | " +
                            amc.getExistingPartner() + " | " + amc.getInitialCostUSD() + " | " +
                            amc.getInitialCostLKR() + " | " + amc.getStartDate()+ " | " + amc.getEndDate()+ " | " +
                            amc.getAmcValueUSD() + " | " + amc.getAmcValueLKR()+ " | " +
                            amc.getAmcPercentageUponPurchasePrice() + " | " + amc.getCategory()+ " | " +
                            amc.getUser().getUsername();

                    userActivityAmcService.saveUserActivity(user,action,filePathBeforeUpdate,filePathAfterUpdate,rowBefore,rowAfter,currentVersion, description);

                    log.info("AMC Contract Add: Success");
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, null, "AMC Contract Added", null),
                            HttpStatus.OK);
                }catch (Exception e){
                    log.error("AMC Contract Add: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("AMC Contract Add: addUpdateAmcDto object is null");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
        }else{
            log.error("AMC Contract Add: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);

        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateAmc(Integer id, AddUpdateAmcDto addUpdateAmcDto) {
        User user = getCurrentUser();
        if(user != null){
            if(id != null){
                addAmcObjectValidator.validate(addUpdateAmcDto);
                if(addUpdateAmcDto != null){
                    try{
                        Optional<Amc> optionalAmc = amcRepository.findById(id);
                        if(optionalAmc.isPresent() && user.equals(optionalAmc.get().getUser())){

                            List<Amc> optionalAmcList = amcRepository.findAmcByContractNameExcludingCurrentAmc(addUpdateAmcDto.getContractName(), id);
                            if(!optionalAmcList.isEmpty()){
                                log.error("Update AMC: Contract Name already existed - " + addUpdateAmcDto.getContractName());
                                return new ResponseEntity<>(
                                        new ApiResponse<>(false, null, "Contract Name already existed - " + addUpdateAmcDto.getContractName(), "EMAIL_ERROR_002"),
                                        HttpStatus.OK);
                            }

                            Amc amc = optionalAmc.get();
                            String action = "update";
                            String basicDescription = amcMapper.getUpdateDescription(amc, addUpdateAmcDto);
                            if(!basicDescription.equals("no Changes")){
                                String description = stringUtils.removeTrailingCommaAndSpace(basicDescription);
                                int currentVersion = userActivityAmcService.findLastId()+1;

                                String formattedAmcStartDateString = dateFormatConverter.convertDateFormat(String.valueOf(amc.getStartDate()));
                                String formattedAmcEndDateString = dateFormatConverter.convertDateFormat(String.valueOf(amc.getEndDate()));

                                String beforeName= "before version " + currentVersion;
                                String filePathBeforeUpdate = amcCsvConverter.generateCsvForAmc(beforeName);
                                String rowBefore = amc.getUserDivision() + " | " + amc.getContractName() + " | " +
                                        amc.getExistingPartner() + " | " + amc.getInitialCostUSD() + " | " +
                                        amc.getInitialCostLKR() + " | " + formattedAmcStartDateString + " | " +
                                        formattedAmcEndDateString+ " | " + amc.getAmcValueUSD() + " | " + amc.getAmcValueLKR()+ " | " +
                                        amc.getAmcPercentageUponPurchasePrice() + " | " + amc.getCategory()+ " | " +
                                        amc.getUser().getUsername();

                                amcRepository.save(amcMapper.addUpdateAmcMapper(amc,addUpdateAmcDto));

                                String afterName= "after version " + currentVersion;
                                String filePathAfterUpdate = amcCsvConverter.generateCsvForAmc(afterName);
                                String rowAfter = amc.getUserDivision() + " | " + amc.getContractName() + " | " +
                                        amc.getExistingPartner() + " | " + amc.getInitialCostUSD() + " | " +
                                        amc.getInitialCostLKR() + " | " + amc.getStartDate()+ " | " + amc.getEndDate()+ " | " +
                                        amc.getAmcValueUSD() + " | " + amc.getAmcValueLKR()+ " | " +
                                        amc.getAmcPercentageUponPurchasePrice() + " | " + amc.getCategory()+ " | " +
                                        amc.getUser().getUsername();

                                userActivityAmcService.saveUserActivity(user,action,filePathBeforeUpdate,filePathAfterUpdate,rowBefore,rowAfter,currentVersion, description);


                                log.info("AMC Contract Update: Success");
                                return new ResponseEntity<>(
                                        new ApiResponse<>(true, null, "AMC Contract Updated", null),
                                        HttpStatus.OK);
                            }
                            log.info("AMC Contract Update: Nothing is changed");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "No changes Found", "AMC_ERROR_001"),
                                    HttpStatus.OK);
                        }
                        log.error("AMC Contract Update: AMC Contract Not Found");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "AMC Contract Not Found", "AMC_ERROR_002"),
                                HttpStatus.OK);
                    }catch (Exception e){
                        log.error("AMC Contract Update: " + e);
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                log.error("AMC Contract Update: addUpdateAmcDto object is null");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
            log.error("AMC Contract Update: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }else{
            log.error("AMC Contract Update: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAmc(Integer id) {
        if(id != null){
            try{
                Optional<Amc> optionalAmc = amcRepository.findById(id);
                if(optionalAmc.isPresent()){
                    Amc amc = optionalAmc.get();
                    log.info("View AMC: AMC Contract Data Retrieved - " + amc.getContractName());
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, amcMapper.userViewMapper(amc), "AMC Contract Data Retrieved - " + amc.getContractName(), null),
                            HttpStatus.OK);
                }
                log.error("View AMC: AMC Contract Not Found");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "AMC Contract Not Found", "AMC_ERROR_002"),
                        HttpStatus.OK);
            }catch (Exception e){
                log.error("View AMC: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View AMC: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAllAmc(int page, int size, String sortBy, boolean ascending) {
        try{
            // Create a Sort object based on the sortBy parameter and direction
            Sort sort = Sort.by(sortBy);
            sort = ascending ? sort.ascending() : sort.descending();

            // Create a Pageable object with the provided page, size, and sort
            Pageable pageable = PageRequest.of(page, size, sort);

            // Retrieve the paginated and sorted results
            Page<Amc> amcList = amcRepository.findAll(pageable);
            List<Amc> amcListCount = amcRepository.findAll();
            int count = amcListCount.size();

            if(amcList.isEmpty()){
                log.error("View All AMC: Empty List");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Empty List", "EMPTY_ERROR_001"),
                        HttpStatus.OK);
            }
            log.info("View All AMC: Listed All AMC List");
            return new ResponseEntity<>(
                    new ApiResponse<>(true, amcList.stream().map(amcMapper::allUsersViewMapper).collect(Collectors.toList()), Integer.toString(count), null),
                    HttpStatus.OK);

        }catch(Exception e){
            log.error("View All AMC: " + e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewMyAmc(Integer id) {
        User user = getCurrentUser();
        if(user != null){
            if(id != null){
                try{
                    Optional<Amc> optionalAmc = amcRepository.findById(id);
                    if(optionalAmc.isPresent() && user.equals(optionalAmc.get().getUser())){
                        Amc amc = optionalAmc.get();
                        log.info("View My AMC: AMC Contract Data Retrieved - " + amc.getContractName());
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, amcMapper.userViewMapper(amc), "AMC Contract Data Retrieved - " + amc.getContractName(), null),
                                HttpStatus.OK);
                    }
                    log.error("View My AMC: AMC Contract Not Found");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "AMC Contract Not Found", "AMC_ERROR_002"),
                            HttpStatus.OK);
                }catch (Exception e){
                    log.error("View My AMC: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);

                }
            }else{
                log.error("View My AMC: Null User ID");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
        }else{
            log.error("View My AMC: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAllMyAmc(int page, int size, String sortBy, boolean ascending) {
        User user = getCurrentUser();
        if(user != null){
            try{
                // Create a Sort object based on the sortBy parameter and direction
                Sort sort = Sort.by(sortBy);
                sort = ascending ? sort.ascending() : sort.descending();

                // Create a Pageable object with the provided page, size, and sort
                Pageable pageable = PageRequest.of(page, size, sort);

                // Retrieve the paginated and sorted results
                Page<Amc> amcList = amcRepository.findAllByUserId(user.getId(), pageable);
                List<Amc> amcListCount = amcRepository.findAllByUserId(user.getId());
                int count = amcListCount.size();

                if(amcList.isEmpty()){
                    log.error("View All My AMC: Empty List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty List", "EMPTY_ERROR_001"),
                            HttpStatus.OK);
                }
                log.info("View All My AMC: Listed All My AMC List");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, amcList.stream().map(amcMapper::allUsersViewMapper).collect(Collectors.toList()), Integer.toString(count), null),
                        HttpStatus.OK);

            }catch (Exception e){
                log.error("View All My AMC: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View All My AMC: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteMyAmc(Integer id) {
        User user = getCurrentUser();
        if(user != null){
            if(id != null){
                try{
                    Optional<Amc> optionalAmc = amcRepository.findById(id);
                    if(optionalAmc.isPresent() && user.equals(optionalAmc.get().getUser())){
                        Amc amc = optionalAmc.get();
                        String action = "delete";
                        String description = user.getEmail() + " Deleted a Row";
                        Integer currentVersion = userActivityAmcService.findLastId()+1;

                        String beforeName= "before version " + currentVersion;
                        String filePathBeforeUpdate = amcCsvConverter.generateCsvForAmc(beforeName);
                        String rowBefore = amc.getUserDivision() + " | " + amc.getContractName() + " | " +
                                amc.getExistingPartner() + " | " + amc.getInitialCostUSD() + " | " +
                                amc.getInitialCostLKR() + " | " + amc.getStartDate()+ " | " + amc.getEndDate()+ " | " +
                                amc.getAmcValueUSD() + " | " + amc.getAmcValueLKR()+ " | " +
                                amc.getAmcPercentageUponPurchasePrice() + " | " + amc.getCategory()+ " | " +
                                amc.getUser().getUsername();

                        amcRepository.deleteById(id);

                        String afterName= "after version " + currentVersion;
                        String filePathAfterUpdate = amcCsvConverter.generateCsvForAmc(afterName);
                        String rowAfter = "";

                        userActivityAmcService.saveUserActivity(user,action,filePathBeforeUpdate,filePathAfterUpdate,rowBefore,rowAfter,currentVersion,description);

                        log.info("Delete My AMC: AMC Contract Data Deleted - " + amc.getContractName());
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, null, "AMC Contract Deleted", null),
                                HttpStatus.OK);
                    }
                    log.error("Delete My AMC: AMC Contract Not Found");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "AMC Contract Not Found", "AMC_ERROR_002"),
                            HttpStatus.OK);
                }catch (Exception e){
                    log.error("Delete My AMC: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("Delete My AMC: Null User ID");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
        }else
        {
            log.error("Delete My AMC: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }
}
