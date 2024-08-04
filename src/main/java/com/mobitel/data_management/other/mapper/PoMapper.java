package com.mobitel.data_management.other.mapper;

import com.mobitel.data_management.dto.requestDto.AddUpdatePoDto;
import com.mobitel.data_management.dto.responseDto.ViewAllPoDto;
import com.mobitel.data_management.dto.responseDto.ViewPoDto;
import com.mobitel.data_management.entity.Po;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class PoMapper {

    public Po addUpdatePoMapper(Po po, AddUpdatePoDto addUpdatePoDto) throws IOException {
        po.setPoNumber(addUpdatePoDto.getPoNumber());
        po.setCreationDate(addUpdatePoDto.getCreationDate());
        po.setPoCreationDate(addUpdatePoDto.getPoCreationDate());
        po.setPoType(addUpdatePoDto.getPoType());
        po.setVendorName(addUpdatePoDto.getVendorName());
        po.setVendorSiteCode(addUpdatePoDto.getVendorSiteCode());
        po.setPoDescription(addUpdatePoDto.getPoDescription());
        po.setApprovalStatus(addUpdatePoDto.getApprovalStatus());
        po.setCurrency(addUpdatePoDto.getCurrency());
        po.setAmount(addUpdatePoDto.getAmount());
        po.setMatchedAmount(addUpdatePoDto.getMatchedAmount());
        po.setBuyerName(addUpdatePoDto.getBuyerName());
        po.setClosureStatus(addUpdatePoDto.getClosureStatus());
        po.setPrNumber(addUpdatePoDto.getPrNumber());
        po.setPrCreationDate(addUpdatePoDto.getPrCreationDate());
        po.setRequisitionHeaderId(addUpdatePoDto.getRequisitionHeaderId());
        po.setRequesterName(addUpdatePoDto.getRequesterName());
        po.setRequesterEmpNum(addUpdatePoDto.getRequesterEmpNum());
        po.setLineNum(addUpdatePoDto.getLineNum());
        po.setItemCode(addUpdatePoDto.getItemCode());
        po.setItemDescription(addUpdatePoDto.getItemDescription());
        po.setLineItemDescription(addUpdatePoDto.getLineItemDescription());
        po.setUnit(addUpdatePoDto.getUnit());
        po.setUnitPrice(addUpdatePoDto.getUnitPrice());
        po.setQuantity(addUpdatePoDto.getQuantity());
        po.setLineAmount(addUpdatePoDto.getLineAmount());
        po.setBudgetAccount(addUpdatePoDto.getBudgetAccount());
        po.setSegment6Desc(addUpdatePoDto.getSegment6Desc());
        po.setPurchaseDeliverToPersonId(addUpdatePoDto.getPurchaseDeliverToPersonId());
        po.setPurchasePoDate(addUpdatePoDto.getPurchasePoDate());
        po.setDepartment(addUpdatePoDto.getDepartment());

        MultipartFile poFile = addUpdatePoDto.getPoFile();
        String filePath = saveFile(poFile);
        po.setPoFile(filePath);

        return po;
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot save empty file.");
        }

        // Define the base URL and the file path
        String fileName = file.getOriginalFilename();
        String baseURL = "http://localhost:8090/PDF-Files/po/";
        String directory = "src/main/resources/static/PDF-Files/po/";
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

    public ViewPoDto viewMapper(Po po) {
        ViewPoDto viewPoDto = new ViewPoDto();
        viewPoDto.setPoNumber(po.getPoNumber());
        viewPoDto.setCreationDate(po.getCreationDate());
        viewPoDto.setPoCreationDate(po.getPoCreationDate());
        viewPoDto.setPoType(po.getPoType());
        viewPoDto.setVendorName(po.getVendorName());
        viewPoDto.setVendorSiteCode(po.getVendorSiteCode());
        viewPoDto.setPoDescription(po.getPoDescription());
        viewPoDto.setApprovalStatus(po.getApprovalStatus());
        viewPoDto.setCurrency(po.getCurrency());
        viewPoDto.setAmount(po.getAmount());
        viewPoDto.setMatchedAmount(po.getMatchedAmount());
        viewPoDto.setBuyerName(po.getBuyerName());
        viewPoDto.setClosureStatus(po.getClosureStatus());
        viewPoDto.setPrNumber(po.getPrNumber());
        viewPoDto.setPrCreationDate(po.getPrCreationDate());
        viewPoDto.setRequisitionHeaderId(po.getRequisitionHeaderId());
        viewPoDto.setRequesterName(po.getRequesterName());
        viewPoDto.setRequesterEmpNum(po.getRequesterEmpNum());
        viewPoDto.setLineNum(po.getLineNum());
        viewPoDto.setItemCode(po.getItemCode());
        viewPoDto.setItemDescription(po.getItemDescription());
        viewPoDto.setLineItemDescription(po.getLineItemDescription());
        viewPoDto.setUnit(po.getUnit());
        viewPoDto.setUnitPrice(po.getUnitPrice());
        viewPoDto.setQuantity(po.getQuantity());
        viewPoDto.setLineAmount(po.getLineAmount());
        viewPoDto.setBudgetAccount(po.getBudgetAccount());
        viewPoDto.setSegment6Desc(po.getSegment6Desc());
        viewPoDto.setPurchaseDeliverToPersonId(po.getPurchaseDeliverToPersonId());
        viewPoDto.setPurchasePoDate(po.getPurchasePoDate());
        viewPoDto.setDepartment(po.getDepartment());
        viewPoDto.setUser(po.getUser().getUsername());
        return viewPoDto;
    }

    public ViewAllPoDto viewAllMapper(Po po){
        ViewAllPoDto viewAllPoDto = new ViewAllPoDto();
        viewAllPoDto.setId(po.getId());
        viewAllPoDto.setPoNumber(po.getPoNumber());
        viewAllPoDto.setCreationDate(po.getCreationDate());
        viewAllPoDto.setPoCreationDate(po.getPoCreationDate());
        viewAllPoDto.setPoType(po.getPoType());
        viewAllPoDto.setVendorName(po.getVendorName());
        viewAllPoDto.setApprovalStatus(po.getApprovalStatus());
        viewAllPoDto.setDepartment(po.getDepartment());
        viewAllPoDto.setPoFile(po.getPoFile());
        viewAllPoDto.setUser(po.getUser().getUsername());
        return viewAllPoDto;
    }
}
