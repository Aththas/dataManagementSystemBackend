package com.mobitel.data_management.service.impl;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.other.mapper.AmcMapper;
import com.mobitel.data_management.other.mapper.UserMapper;
import com.mobitel.data_management.other.validator.ObjectValidator;
import com.mobitel.data_management.repository.AmcRepository;
import com.mobitel.data_management.service.AmcService;
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

    private User getCurrentUser(){
        final String userEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        return optionalUser.orElse(null);
    }
    @Override
    public ResponseEntity<String> addAmc(AddUpdateAmcDto addUpdateAmcDto) {
        User user = getCurrentUser();
        if(user != null){
            addAmcObjectValidator.validate(addUpdateAmcDto);
            if(addUpdateAmcDto != null){
                try{
                    Optional<Amc> optionalAmc = amcRepository.findByContractName(addUpdateAmcDto.getContractName());
                    if(optionalAmc.isPresent()){
                        log.error("AMC Contract Add: Contract Name Already Exist");
                        return new ResponseEntity<>("Contract Name Already Exist",HttpStatus.OK);
                    }

                    Amc amc = new Amc();
                    amc.setUser(user);
                    amcRepository.save(amcMapper.addUpdateAmcMapper(amc,addUpdateAmcDto));

                    log.info("AMC Contract Add: Success");
                    return new ResponseEntity<>("AMC Contractor Added",HttpStatus.CREATED);
                }catch (Exception e){
                    log.error("AMC Contract Add: " + e);
                    return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("AMC Contract Add: addUpdateAmcDto object is null");
                return new ResponseEntity<>("Null values are not permitted", HttpStatus.BAD_REQUEST);
            }
        }else{
            log.error("AMC Contract Add: Unauthorized Access");
            return new ResponseEntity<>("Unauthorized Access", HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    public ResponseEntity<?> viewAmc(Integer id) {
        if(id != null){
            try{
                Optional<Amc> optionalAmc = amcRepository.findById(id);
                if(optionalAmc.isPresent()){
                    Amc amc = optionalAmc.get();
                    log.info("View AMC: AMC Contract Data Retrieved - " + amc.getContractName());
                    return new ResponseEntity<>(amcMapper.userViewMapper(amc),HttpStatus.OK);
                }
                log.error("View AMC: AMC Contract Not Found");
                return new ResponseEntity<>("AMC Contract Not Found",HttpStatus.OK);
            }catch (Exception e){
                log.error("View AMC: " + e);
                return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            log.error("View AMC: Null User ID");
            return new ResponseEntity<>("Null User ID",HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> viewAllAmc(int page, int size, String sortBy, boolean ascending) {
        try{
            // Create a Sort object based on the sortBy parameter and direction
            Sort sort = Sort.by(sortBy);
            sort = ascending ? sort.ascending() : sort.descending();

            // Create a Pageable object with the provided page, size, and sort
            Pageable pageable = PageRequest.of(page, size, sort);

            // Retrieve the paginated and sorted results
            Page<Amc> amcList = amcRepository.findAll(pageable);

            if(amcList.isEmpty()){
                log.error("View All AMC: Empty List");
                return new ResponseEntity<>("Empty List",HttpStatus.OK);
            }
            log.info("View All AMC: Listed All AMC List");
            return new ResponseEntity<>(amcList.stream().map(amcMapper::userViewMapper).collect(Collectors.toList()), HttpStatus.OK);

        }catch(Exception e){
            log.error("View All AMC: " + e);
            return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> viewMyAmc(Integer id) {
        User user = getCurrentUser();
        if(user != null){
            if(id != null){
                try{
                    Optional<Amc> optionalAmc = amcRepository.findById(id);
                    if(optionalAmc.isPresent() && user.equals(optionalAmc.get().getUser())){
                        Amc amc = optionalAmc.get();
                        log.info("View My AMC: AMC Contract Data Retrieved - " + amc.getContractName());
                        return new ResponseEntity<>(amcMapper.userViewMapper(amc),HttpStatus.OK);
                    }
                    log.error("View My AMC: AMC Contract Not Found");
                    return new ResponseEntity<>("AMC Contract Not Found",HttpStatus.OK);
                }catch (Exception e){
                    log.error("View My AMC: " + e);
                    return new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                log.error("View My AMC: Null User ID");
                return new ResponseEntity<>("Null User ID",HttpStatus.BAD_REQUEST);
            }
        }else{
            log.error("View My AMC: Unauthorized Access");
            return new ResponseEntity<>("Unauthorized Access", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<?> viewAllMyAmc(int page, int size, String sortBy, boolean ascending) {
        User user = getCurrentUser();
        if(user != null){
            // Create a Sort object based on the sortBy parameter and direction
            Sort sort = Sort.by(sortBy);
            sort = ascending ? sort.ascending() : sort.descending();

            // Create a Pageable object with the provided page, size, and sort
            Pageable pageable = PageRequest.of(page, size, sort);

            // Retrieve the paginated and sorted results
            Page<Amc> amcList = amcRepository.findAllByUserId(user.getId(), pageable);

            if(amcList.isEmpty()){
                log.error("View All My AMC: Empty List");
                return new ResponseEntity<>("Empty List",HttpStatus.OK);
            }
            log.info("View All My AMC: Listed All My AMC List");
            return new ResponseEntity<>(amcList.stream().map(amcMapper::userViewMapper).collect(Collectors.toList()), HttpStatus.OK);

        }else{
            log.error("View All My AMC: Unauthorized Access");
            return new ResponseEntity<>("Unauthorized Access", HttpStatus.UNAUTHORIZED);
        }
    }
}
