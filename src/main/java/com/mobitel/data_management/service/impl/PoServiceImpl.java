package com.mobitel.data_management.service.impl;

import com.mobitel.data_management.auth.entity.user.Role;
import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.entity.user.UserGroup;
import com.mobitel.data_management.auth.repository.UserGroupRepository;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.dto.requestDto.AddUpdatePoDto;
import com.mobitel.data_management.entity.Po;
import com.mobitel.data_management.other.apiResponseDto.ApiResponse;
import com.mobitel.data_management.other.csvService.PoCSVConverter;
import com.mobitel.data_management.other.dateUtility.DateFormatConverter;
import com.mobitel.data_management.other.mapper.PoMapper;
import com.mobitel.data_management.other.stringUtility.StringUtils;
import com.mobitel.data_management.other.validator.ObjectValidator;
import com.mobitel.data_management.repository.PoRepository;
import com.mobitel.data_management.service.PoService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoServiceImpl implements PoService {
    private final UserRepository userRepository;
    private final PoRepository poRepository;
    private final PoMapper poMapper;
    private final ObjectValidator<AddUpdatePoDto> addUpdatePoDtoObjectValidator;
    private final UserActivityPoService userActivityPoService;
    private final PoCSVConverter poCSVConverter;
    private final StringUtils stringUtils;
    private final DateFormatConverter dateFormatConverter;
    private final UserGroupRepository userGroupRepository;
    private User getCurrentUser(){
        final String userEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        return optionalUser.orElse(null);
    }
    @Override
    public ResponseEntity<ApiResponse<?>> addPo(AddUpdatePoDto addUpdatePoDto) {
        User user = getCurrentUser();
        if(user != null){
            addUpdatePoDtoObjectValidator.validate(addUpdatePoDto);
            if(addUpdatePoDto != null){
                try{
                    if(addUpdatePoDto.getPoFile() == null){
                        log.error("PO Add: PO File Not Found");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "PO File Not Found", "PO_FILE_NOT_FOUND_ERROR_001"),
                                HttpStatus.OK);
                    }
                    if(!poMapper.isPdfFile(addUpdatePoDto.getPoFile())){
                        log.error("PO Add: Accept pdf files only");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Accept pdf files only", "PO_FILE_NOT_FOUND_ERROR_001"),
                                HttpStatus.OK);
                    }

                    Optional<Po> optionalPo = poRepository.findByPoNumber(addUpdatePoDto.getPoNumber());
                    if(optionalPo.isPresent()){
                        log.error("PO Add: PO number Already Exist");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "PO number Already Exist", "PO_ERROR_001"),
                                HttpStatus.OK);
                    }

                    String action = "add";
                    String description = user.getEmail() + " Added a Row";
                    Integer currentVersion = userActivityPoService.findLastId()+1;

                    String beforeName= "before version " + currentVersion;
                    String filePathBeforeUpdate =poCSVConverter.generateCsvForPo(beforeName);
                    String rowBefore = "";

                    Po po = new Po();
                    MultipartFile poFile = addUpdatePoDto.getPoFile();
                    String filePath = poMapper.saveFile(poFile);
                    po.setPoFile(filePath);
                    po.setUser(user);
                    poRepository.save(poMapper.addUpdatePoMapper(po,addUpdatePoDto));

                    String afterName= "after version " + currentVersion;
                    String filePathAfterUpdate = poCSVConverter.generateCsvForPo(afterName);
                    String rowAfter = po.getPoNumber() + " | " + po.getCreationDate() + " | " + po.getPoCreationDate() + " | " +
                            po.getPoType() + " | " + po.getVendorName() + " | " + po.getVendorSiteCode() + " | " + po.getPoDescription() + " | " +
                            po.getApprovalStatus() + " | " + po.getCurrency() + " | " + po.getAmount() + " | " + po.getMatchedAmount() + " | " +
                            po.getBuyerName() + " | " + po.getClosureStatus() + " | " + po.getPrNumber() + " | " + po.getPrCreationDate() + " | " +
                            po.getRequisitionHeaderId() + " | " + po.getRequesterName() + " | " + po.getRequesterEmpNum() + " | " + po.getLineNum() + " | " +
                            po.getItemCode() + " | " + po.getItemDescription() + " | " + po.getLineItemDescription() + " | " + po.getUnit() + " | " +
                            po.getUnitPrice() + " | " + po.getQuantity() + " | " + po.getLineAmount() + " | " + po.getBudgetAccount() + " | " +
                            po.getSegment6Desc() + " | " + po.getPurchaseDeliverToPersonId() + " | " + po.getPurchasePoDate() + " | " +
                            po.getDepartment() + " | " + po.getUser().getUsername();

                    userActivityPoService.saveUserActivity(user,action,filePathBeforeUpdate,filePathAfterUpdate,rowBefore,rowAfter,currentVersion, description);

                    log.info("PO Add: Success");
                    return new ResponseEntity<>(
                            new ApiResponse<>(true, null, "PO Added", null),
                            HttpStatus.OK);
                }catch (Exception e){
                    log.error("PO Add: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("PO Add: addUpdatePoDto object is null");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
        }else{
            log.error("PO Add: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updatePo(Integer id, AddUpdatePoDto addUpdatePoDto) {
        User user = getCurrentUser();
        if(user != null){
            if(id != null){
                addUpdatePoDtoObjectValidator.validate(addUpdatePoDto);
                if(addUpdatePoDto != null){
                    try{
                        if(addUpdatePoDto.getPoFile() != null && !poMapper.isPdfFile(addUpdatePoDto.getPoFile())){
                            log.error("PO Update: Accept pdf files only");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "Accept pdf files only", "PO_FILE_NOT_FOUND_ERROR_001"),
                                    HttpStatus.OK);
                        }
                        Optional<Po> optionalPo = poRepository.findById(id);
                        if(optionalPo.isPresent() && user.equals(optionalPo.get().getUser())){
                            Po po = optionalPo.get();

                            List<Po> optionalPoList = poRepository.findPoByPoNumberExcludingCurrentPo(addUpdatePoDto.getPoNumber(), id);
                            if(!optionalPoList.isEmpty()){
                                log.error("Update PO: PO Number already existed - " + addUpdatePoDto.getPoNumber());
                                return new ResponseEntity<>(
                                        new ApiResponse<>(false, null, "PO Number already existed - " + addUpdatePoDto.getPoNumber(), "EMAIL_ERROR_002"),
                                        HttpStatus.OK);
                            }

                            String action = "update";
                            String basicDescription = poMapper.getUpdateDescription(po, addUpdatePoDto);
                            if(!basicDescription.equals("no Changes")){
                                String description = stringUtils.removeTrailingCommaAndSpace(basicDescription);
                                int currentVersion = userActivityPoService.findLastId()+1;

                                String formattedCreationDate = dateFormatConverter.convertDateFormat(String.valueOf(po.getCreationDate()));
                                String formattedPoCreationDate = dateFormatConverter.convertDateFormat(String.valueOf(po.getPoCreationDate()));
                                String formattedPrCreationDate = dateFormatConverter.convertDateFormat(String.valueOf(po.getPrCreationDate()));
                                String formattedPurchasePoDate = dateFormatConverter.convertDateFormat(String.valueOf(po.getPurchasePoDate()));

                                String beforeName= "before version " + currentVersion;
                                String filePathBeforeUpdate = poCSVConverter.generateCsvForPo(beforeName);
                                String rowBefore = po.getPoNumber() + " | " + formattedCreationDate + " | " + formattedPoCreationDate + " | " +
                                        po.getPoType() + " | " + po.getVendorName() + " | " + po.getVendorSiteCode() + " | " + po.getPoDescription() + " | " +
                                        po.getApprovalStatus() + " | " + po.getCurrency() + " | " + po.getAmount() + " | " + po.getMatchedAmount() + " | " +
                                        po.getBuyerName() + " | " + po.getClosureStatus() + " | " + po.getPrNumber() + " | " + formattedPrCreationDate + " | " +
                                        po.getRequisitionHeaderId() + " | " + po.getRequesterName() + " | " + po.getRequesterEmpNum() + " | " + po.getLineNum() + " | " +
                                        po.getItemCode() + " | " + po.getItemDescription() + " | " + po.getLineItemDescription() + " | " + po.getUnit() + " | " +
                                        po.getUnitPrice() + " | " + po.getQuantity() + " | " + po.getLineAmount() + " | " + po.getBudgetAccount() + " | " +
                                        po.getSegment6Desc() + " | " + po.getPurchaseDeliverToPersonId() + " | " + formattedPurchasePoDate + " | " +
                                        po.getDepartment() + " | " +  po.getUser().getUsername();

                                String filePath;
                                if(addUpdatePoDto.getPoFile() == null){
                                    filePath = po.getPoFile();
                                }else{
                                    MultipartFile poFile = addUpdatePoDto.getPoFile();
                                    filePath  = poMapper.saveFile(poFile);
                                }
                                po.setPoFile(filePath);
                                poRepository.save(poMapper.addUpdatePoMapper(po,addUpdatePoDto));

                                String afterName= "after version " + currentVersion;
                                String filePathAfterUpdate = poCSVConverter.generateCsvForPo(afterName);
                                String rowAfter = po.getPoNumber() + " | " + formattedCreationDate + " | " + formattedPoCreationDate + " | " +
                                        po.getPoType() + " | " + po.getVendorName() + " | " + po.getVendorSiteCode() + " | " + po.getPoDescription() + " | " +
                                        po.getApprovalStatus() + " | " + po.getCurrency() + " | " + po.getAmount() + " | " + po.getMatchedAmount() + " | " +
                                        po.getBuyerName() + " | " + po.getClosureStatus() + " | " + po.getPrNumber() + " | " + formattedPrCreationDate + " | " +
                                        po.getRequisitionHeaderId() + " | " + po.getRequesterName() + " | " + po.getRequesterEmpNum() + " | " + po.getLineNum() + " | " +
                                        po.getItemCode() + " | " + po.getItemDescription() + " | " + po.getLineItemDescription() + " | " + po.getUnit() + " | " +
                                        po.getUnitPrice() + " | " + po.getQuantity() + " | " + po.getLineAmount() + " | " + po.getBudgetAccount() + " | " +
                                        po.getSegment6Desc() + " | " + po.getPurchaseDeliverToPersonId() + " | " + formattedPurchasePoDate + " | " +
                                        po.getDepartment() + " | " +  po.getUser().getUsername();

                                userActivityPoService.saveUserActivity(user,action,filePathBeforeUpdate,filePathAfterUpdate,rowBefore,rowAfter,currentVersion, description);


                                log.info("PO Update: Success");
                                return new ResponseEntity<>(
                                        new ApiResponse<>(true, null, "PO Updated", null),
                                        HttpStatus.OK);
                            }
                            log.info("PO Update: Nothing is changed");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "No changes Found", "PO_ERROR_001"),
                                    HttpStatus.OK);
                        }
                        log.error("PO Update: PO Not Found");
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "PO Not Found", "PO_ERROR_002"),
                                HttpStatus.OK);
                    }catch (Exception e){
                        log.error("PO Update: " + e);
                        return new ResponseEntity<>(
                                new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                log.error("PO Update: addUpdatePoDto object is null");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null Values Not Permitted", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
            log.error("PO Update: Null User ID");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                    HttpStatus.BAD_REQUEST);
        }else{
            log.error("PO Update: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewPo(Integer id) {
        User user = getCurrentUser();
        if(user != null){
            if(id != null){
                try{
                    Optional<Po> optionalPo = poRepository.findById(id);
                    if(optionalPo.isPresent()){
                        Po po = optionalPo.get();

                        String poOwnerGrp = po.getUser().getGrpName();
                        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByUserIdAndGrpName(user.getId(), poOwnerGrp);
                        if(po.getUser().equals(user) || user.getRole().equals(Role.ADMIN) || optionalUserGroup.isPresent()){
                            log.info("View PO: PO Data Retrieved - " + po.getPoNumber());
                            return new ResponseEntity<>(
                                    new ApiResponse<>(true, poMapper.viewMapper(po), "PO Data Retrieved - " + po.getPoNumber(), null),
                                    HttpStatus.OK);
                        }else{
                            log.error("View PO: Restricted View Access");
                            return new ResponseEntity<>(
                                    new ApiResponse<>(false, null, "Restricted View Access", "PO_ERROR_002"),
                                    HttpStatus.OK);
                        }

                    }
                    log.error("View PO: PO Not Found");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "PO Not Found", "AMC_ERROR_002"),
                            HttpStatus.OK);
                }catch (Exception e){
                    log.error("View PO: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("View PO: Null User ID");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
        }else{
            log.error("View PO: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAllPo(int page, int size, String sortBy, boolean ascending) {
        try{
            // Create a Sort object based on the sortBy parameter and direction
            Sort sort = Sort.by(sortBy);
            sort = ascending ? sort.ascending() : sort.descending();

            // Create a Pageable object with the provided page, size, and sort
            Pageable pageable = PageRequest.of(page, size, sort);

            // Retrieve the paginated and sorted results
            Page<Po> poList = poRepository.findAll(pageable);
            List<Po> poListCount = poRepository.findAll();
            int count = poListCount.size();

            if(poList.isEmpty()){
                log.error("View All PO: Empty List");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Empty List", "EMPTY_ERROR_001"),
                        HttpStatus.OK);
            }
            log.info("View All PO: Listed All PO List");
            return new ResponseEntity<>(
                    new ApiResponse<>(true, poList.stream().map(poMapper::viewAllMapper).collect(Collectors.toList()), Integer.toString(count), null),
                    HttpStatus.OK);

        }catch(Exception e){
            log.error("View All PO: " + e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> viewAllMyPo(int page, int size, String sortBy, boolean ascending) {
        User user = getCurrentUser();
        if(user != null){
            try{
                // Create a Sort object based on the sortBy parameter and direction
                Sort sort = Sort.by(sortBy);
                sort = ascending ? sort.ascending() : sort.descending();

                // Create a Pageable object with the provided page, size, and sort
                Pageable pageable = PageRequest.of(page, size, sort);

                // Retrieve the paginated and sorted results
                Page<Po> poList = poRepository.findAllByUserId(user.getId(), pageable);
                List<Po> poListCount = poRepository.findAllByUserId(user.getId());
                int count = poListCount.size();

                if(poList.isEmpty()){
                    log.error("View All My PO: Empty List");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Empty List", "EMPTY_ERROR_001"),
                            HttpStatus.OK);
                }
                log.info("View All My PO: Listed All My PO List");
                return new ResponseEntity<>(
                        new ApiResponse<>(true, poList.stream().map(poMapper::viewAllMapper).collect(Collectors.toList()), Integer.toString(count), null),
                        HttpStatus.OK);

            }catch (Exception e){
                log.error("View All My PO: " + e);
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View All My PO: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteMyPo(Integer id) {
        User user = getCurrentUser();
        if(user != null){
            if(id != null){
                try{
                    Optional<Po> optionalPo = poRepository.findById(id);
                    if(optionalPo.isPresent() && user.equals(optionalPo.get().getUser())){
                        Po po = optionalPo.get();
                        String action = "delete";
                        String description = user.getEmail() + " Deleted a Row";
                        Integer currentVersion = userActivityPoService.findLastId()+1;

                        String beforeName= "before version " + currentVersion;
                        String filePathBeforeUpdate =poCSVConverter.generateCsvForPo(beforeName);
                        String rowBefore = po.getPoNumber() + " | " + po.getCreationDate() + " | " + po.getPoCreationDate() + " | " +
                                po.getPoType() + " | " + po.getVendorName() + " | " + po.getVendorSiteCode() + " | " + po.getPoDescription() + " | " +
                                po.getApprovalStatus() + " | " + po.getCurrency() + " | " + po.getAmount() + " | " + po.getMatchedAmount() + " | " +
                                po.getBuyerName() + " | " + po.getClosureStatus() + " | " + po.getPrNumber() + " | " + po.getPrCreationDate() + " | " +
                                po.getRequisitionHeaderId() + " | " + po.getRequesterName() + " | " + po.getRequesterEmpNum() + " | " + po.getLineNum() + " | " +
                                po.getItemCode() + " | " + po.getItemDescription() + " | " + po.getLineItemDescription() + " | " + po.getUnit() + " | " +
                                po.getUnitPrice() + " | " + po.getQuantity() + " | " + po.getLineAmount() + " | " + po.getBudgetAccount() + " | " +
                                po.getSegment6Desc() + " | " + po.getPurchaseDeliverToPersonId() + " | " + po.getPurchasePoDate() + " | " +
                                po.getDepartment() + " | " + po.getUser().getUsername();

                        poRepository.deleteById(id);

                        String afterName= "after version " + currentVersion;
                        String filePathAfterUpdate = poCSVConverter.generateCsvForPo(afterName);
                        String rowAfter = "";

                        userActivityPoService.saveUserActivity(user,action,filePathBeforeUpdate,filePathAfterUpdate,rowBefore,rowAfter,currentVersion, description);



                        log.info("Delete My PO: PO Data Deleted - " + po.getPoNumber());
                        return new ResponseEntity<>(
                                new ApiResponse<>(true, null, "PO Deleted", null),
                                HttpStatus.OK);
                    }
                    log.error("Delete My PO: PO Not Found");
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "PO Not Found", "PO_ERROR_002"),
                            HttpStatus.OK);
                }catch (Exception e){
                    log.error("Delete My PO: " + e);
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, null, "Server Error", "SERVER_ERROR_500"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("Delete My PO: Null User ID");
                return new ResponseEntity<>(
                        new ApiResponse<>(false, null, "Null User ID", "NULL_ERROR_100"),
                        HttpStatus.BAD_REQUEST);
            }
        }else
        {
            log.error("Delete My PO: Unauthorized Access");
            return new ResponseEntity<>(
                    new ApiResponse<>(false, null, "Unauthorized Access", "AUTH_ERROR_001"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

}
