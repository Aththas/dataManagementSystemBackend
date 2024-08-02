package com.mobitel.data_management.other.mapper;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.dto.responseDto.ViewAllAmcDto;
import com.mobitel.data_management.dto.responseDto.ViewAmcDto;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.other.dateUtility.DateFormatConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AmcMapper {
    private final DateFormatConverter dateFormatConverter;

    public Amc addUpdateAmcMapper(Amc amc, AddUpdateAmcDto addUpdateAmcDto){
        amc.setUserDivision(addUpdateAmcDto.getUserDivision());
        amc.setContractName(addUpdateAmcDto.getContractName());
        amc.setExistingPartner(addUpdateAmcDto.getExistingPartner());
        amc.setInitialCostUSD(addUpdateAmcDto.getInitialCostUSD());
        amc.setInitialCostLKR(addUpdateAmcDto.getInitialCostLKR());
        amc.setStartDate(addUpdateAmcDto.getStartDate());
        amc.setEndDate(addUpdateAmcDto.getEndDate());
        amc.setAmcValueUSD(addUpdateAmcDto.getAmcValueUSD());
        amc.setAmcValueLKR(addUpdateAmcDto.getAmcValueLKR());
        amc.setAmcPercentageUponPurchasePrice(addUpdateAmcDto.getAmcPercentageUponPurchasePrice());
        amc.setCategory(addUpdateAmcDto.getCategory());
        return amc;
    }

    public String getUpdateDescription(Amc amc, AddUpdateAmcDto addUpdateAmcDto) {
        String formattedAmcStartDateString = dateFormatConverter.convertDateFormat(String.valueOf(amc.getStartDate()));
        String formattedAmcEndDateString = dateFormatConverter.convertDateFormat(String.valueOf(amc.getEndDate()));
        String description = "These fields are changed: ";
        int changeCount = 0;
        if(!amc.getUserDivision().equals(addUpdateAmcDto.getUserDivision())){
            description = description + "User Division updated from " + amc.getUserDivision() + " to " +addUpdateAmcDto.getUserDivision() + ", ";
            changeCount++;
        }
        if(!amc.getContractName().equals(addUpdateAmcDto.getContractName())){
            description = description + "Contract name updated from " + amc.getContractName() + " to " +addUpdateAmcDto.getContractName() + ", ";
            changeCount++;
        }
        if(!amc.getExistingPartner().equals(addUpdateAmcDto.getExistingPartner())){
            description = description + "Existing Partner updated from " + amc.getExistingPartner() + " to " +addUpdateAmcDto.getExistingPartner() + ", ";
            changeCount++;
        }
        if(amc.getInitialCostUSD() != addUpdateAmcDto.getInitialCostUSD()){
            description = description + "Initial Cost USD updated from " + amc.getInitialCostUSD() + " to " +addUpdateAmcDto.getInitialCostUSD() + ", ";
            changeCount++;
        }
        if(amc.getInitialCostLKR() != addUpdateAmcDto.getInitialCostLKR()){
            description = description + "Initial Cost LKR updated from " + amc.getInitialCostLKR() + " to " +addUpdateAmcDto.getInitialCostLKR() + ", ";
            changeCount++;
        }
        if(!formattedAmcStartDateString.equals(String.valueOf(addUpdateAmcDto.getStartDate()))){
            description = description + "Start Date updated from " + amc.getStartDate() + " to " +addUpdateAmcDto.getStartDate() + ", ";
            changeCount++;
        }
        if(!formattedAmcEndDateString.equals(String.valueOf(addUpdateAmcDto.getEndDate()))){
            description = description + "End Date updated from " + amc.getEndDate() + " to " +addUpdateAmcDto.getEndDate() + ", ";
            changeCount++;
        }
        if(amc.getAmcValueUSD() != addUpdateAmcDto.getAmcValueUSD()){
            description = description + "AMC Value USD Updated from " + amc.getAmcValueUSD() + " to " +addUpdateAmcDto.getAmcValueUSD() + ", ";
            changeCount++;
        }
        if(amc.getAmcValueLKR() != addUpdateAmcDto.getAmcValueLKR()){
            description = description + "AMC Value LKR updated from " + amc.getAmcValueLKR() + " to " +addUpdateAmcDto.getAmcValueLKR() + ", ";
            changeCount++;
        }
        if(amc.getAmcPercentageUponPurchasePrice() != addUpdateAmcDto.getAmcPercentageUponPurchasePrice()){
            description = description + "AMC % Upon Purchase Price updated from " + amc.getAmcPercentageUponPurchasePrice() + " to " +addUpdateAmcDto.getAmcPercentageUponPurchasePrice() + ", ";
            changeCount++;
        }
        if(!amc.getCategory().equals(addUpdateAmcDto.getCategory())){
            description = description + "Category updated from " + amc.getCategory() + " to " +addUpdateAmcDto.getCategory() + ", ";
            changeCount++;
        }

        if (changeCount == 0){
            return "no Changes";
        }else{
            return description;
        }
    }

    public ViewAmcDto userViewMapper(Amc amc) {
        ViewAmcDto viewAmcDto = new ViewAmcDto();
        viewAmcDto.setUserDivision(amc.getUserDivision());
        viewAmcDto.setContractName(amc.getContractName());
        viewAmcDto.setExistingPartner(amc.getExistingPartner());
        viewAmcDto.setInitialCostUSD(amc.getInitialCostUSD());
        viewAmcDto.setInitialCostLKR(amc.getInitialCostLKR());
        viewAmcDto.setStartDate(amc.getStartDate());
        viewAmcDto.setEndDate(amc.getEndDate());
        viewAmcDto.setAmcValueUSD(amc.getAmcValueUSD());
        viewAmcDto.setAmcValueLKR(amc.getAmcValueLKR());
        viewAmcDto.setAmcPercentageUponPurchasePrice(amc.getAmcPercentageUponPurchasePrice());
        viewAmcDto.setCategory(amc.getCategory());
        viewAmcDto.setUser(amc.getUser().getUsername());
        return viewAmcDto;
    }

    public ViewAllAmcDto allUsersViewMapper(Amc amc) {
        ViewAllAmcDto viewAmcDto = new ViewAllAmcDto();
        viewAmcDto.setId(amc.getId());
        viewAmcDto.setUserDivision(amc.getUserDivision());
        viewAmcDto.setContractName(amc.getContractName());
        viewAmcDto.setInitialCostUSD(amc.getInitialCostUSD());
        viewAmcDto.setInitialCostLKR(amc.getInitialCostLKR());
        viewAmcDto.setStartDate(amc.getStartDate());
        viewAmcDto.setEndDate(amc.getEndDate());
        viewAmcDto.setCategory(amc.getCategory());
        viewAmcDto.setUser(amc.getUser().getUsername());
        return viewAmcDto;
    }

}
