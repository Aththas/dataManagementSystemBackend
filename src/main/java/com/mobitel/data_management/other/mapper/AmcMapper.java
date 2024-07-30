package com.mobitel.data_management.other.mapper;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.dto.responseDto.ViewAmcDto;
import com.mobitel.data_management.entity.Amc;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AmcMapper {

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
}
