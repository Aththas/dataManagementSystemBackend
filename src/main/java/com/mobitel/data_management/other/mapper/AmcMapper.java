package com.mobitel.data_management.other.mapper;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.dto.responseDto.ViewAllAmcDto;
import com.mobitel.data_management.dto.responseDto.ViewAmcDto;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.other.dateUtility.DateFormatConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@Slf4j
@RequiredArgsConstructor
public class AmcMapper {
    private final DateFormatConverter dateFormatConverter;

    @Value("${spring.application.security.backendUrl}")
    private String backendUrl;

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
        String description = "These fields are changed: \n";
        int changeCount = 0;
        if(!amc.getUserDivision().equals(addUpdateAmcDto.getUserDivision())){
            description = description + (changeCount+1) +". User Division updated from '" + amc.getUserDivision() + "' to '" +addUpdateAmcDto.getUserDivision() + "'\n";
            changeCount++;
        }
        if(!amc.getContractName().equals(addUpdateAmcDto.getContractName())){
            description = description + (changeCount+1) +". Contract name updated from '" + amc.getContractName() + "' to '" +addUpdateAmcDto.getContractName() + "'\n";
            changeCount++;
        }
        if(!amc.getExistingPartner().equals(addUpdateAmcDto.getExistingPartner())){
            description = description + (changeCount+1) +". Existing Partner updated from '" + amc.getExistingPartner() + "' to '" +addUpdateAmcDto.getExistingPartner() + "'\n";
            changeCount++;
        }
        if(amc.getInitialCostUSD() != addUpdateAmcDto.getInitialCostUSD()){
            description = description +(changeCount+1) + ". Initial Cost USD updated from '" + amc.getInitialCostUSD() + "' to '" +addUpdateAmcDto.getInitialCostUSD() + "'\n";
            changeCount++;
        }
        if(amc.getInitialCostLKR() != addUpdateAmcDto.getInitialCostLKR()){
            description = description + (changeCount+1) +". Initial Cost LKR updated from '" + amc.getInitialCostLKR() + "' to '" +addUpdateAmcDto.getInitialCostLKR() + "'\n";
            changeCount++;
        }
        if(!formattedAmcStartDateString.equals(String.valueOf(addUpdateAmcDto.getStartDate()))){
            description = description + (changeCount+1) +". Start Date updated from '" + amc.getStartDate() + "' to '" +addUpdateAmcDto.getStartDate() + "'\n";
            changeCount++;
        }
        if(!formattedAmcEndDateString.equals(String.valueOf(addUpdateAmcDto.getEndDate()))){
            description = description + (changeCount+1) +". End Date updated from '" + amc.getEndDate() + "' to '" +addUpdateAmcDto.getEndDate() + "'\n";
            changeCount++;
        }
        if(amc.getAmcValueUSD() != addUpdateAmcDto.getAmcValueUSD()){
            description = description + (changeCount+1) +". AMC Value USD Updated from '" + amc.getAmcValueUSD() + "' to '" +addUpdateAmcDto.getAmcValueUSD() + "'\n";
            changeCount++;
        }
        if(amc.getAmcValueLKR() != addUpdateAmcDto.getAmcValueLKR()){
            description = description + (changeCount+1) +". AMC Value LKR updated from '" + amc.getAmcValueLKR() + "' to '" +addUpdateAmcDto.getAmcValueLKR() + "'\n";
            changeCount++;
        }
        if(amc.getAmcPercentageUponPurchasePrice() != addUpdateAmcDto.getAmcPercentageUponPurchasePrice()){
            description = description + (changeCount+1) +". AMC % Upon Purchase Price updated from '" + amc.getAmcPercentageUponPurchasePrice() + "' to '" +addUpdateAmcDto.getAmcPercentageUponPurchasePrice() + "'\n";
            changeCount++;
        }
        if(!amc.getCategory().equals(addUpdateAmcDto.getCategory())){
            description = description + (changeCount+1) +". Category updated from '" + amc.getCategory() + "' to '" +addUpdateAmcDto.getCategory() + "'\n";
            changeCount++;
        }
        if(addUpdateAmcDto.getAmcFile() != null){
            description += (changeCount + 1) + ". File Changed'\n";
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
        viewAmcDto.setAmcFile(amc.getAmcFile());
        viewAmcDto.setUser(amc.getUser().getUsername());
        return viewAmcDto;
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot save empty file.");
        }

        // Define the base URL and the file path
        String fileName = file.getOriginalFilename();
        String baseURL = backendUrl + "/PDF-Files/amc/";
        String directory = "src/main/resources/static/PDF-Files/amc/";
        String filePath = directory + fileName;

        // Create directories if they don't exist
        Path directoryPath = Paths.get(directory);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Save the file to the specified path
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }

        return baseURL + fileName;
    }

    public boolean isPdfFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            return extension.equalsIgnoreCase("pdf");
        }
        return false;
    }

    public String setRowDetails(Amc amc){

        return amc.getUserDivision() + " | " + amc.getContractName() + " | " +
                amc.getExistingPartner() + " | " + amc.getInitialCostUSD() + " | " +
                amc.getInitialCostLKR() + " | " + amc.getStartDate() + " | " +
                amc.getEndDate() + " | " + amc.getAmcValueUSD() + " | " + amc.getAmcValueLKR()+ " | " +
                amc.getAmcPercentageUponPurchasePrice() + " | " + amc.getCategory()+ " | " +
                amc.getUser().getUsername();
    }

}
