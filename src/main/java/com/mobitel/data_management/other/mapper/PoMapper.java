package com.mobitel.data_management.other.mapper;

import com.mobitel.data_management.dto.requestDto.AddUpdatePoDto;
import com.mobitel.data_management.dto.responseDto.ViewAllPoDto;
import com.mobitel.data_management.dto.responseDto.ViewPoDto;
import com.mobitel.data_management.entity.Po;
import com.mobitel.data_management.other.dateUtility.DateFormatConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
@RequiredArgsConstructor
public class PoMapper {
    private final DateFormatConverter dateFormatConverter;

    @Value("${spring.application.security.backendUrl}")
    private String backendUrl;

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
        return po;
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot save empty file.");
        }

        // Define the base URL and the file path
        String fileName = file.getOriginalFilename();
        String baseURL = backendUrl + "/PDF-Files/po/";
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

    public boolean isPdfFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            return extension.equalsIgnoreCase("pdf");
        }
        return false;
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

    public String getUpdateDescription(Po po, AddUpdatePoDto addUpdatePoDto) {
        String formattedCreationDate = dateFormatConverter.convertDateFormat(String.valueOf(po.getCreationDate()));
        String formattedPoCreationDate = dateFormatConverter.convertDateFormat(String.valueOf(po.getPoCreationDate()));
        String formattedPrCreationDate = dateFormatConverter.convertDateFormat(String.valueOf(po.getPrCreationDate()));
        String formattedPurchasePoDate = dateFormatConverter.convertDateFormat(String.valueOf(po.getPurchasePoDate()));

        log.error(formattedCreationDate);
        log.error(addUpdatePoDto.getCreationDate().toString());

        String description = "These fields are changed: \n";
        int changeCount = 0;

        if (po.getPoNumber() != addUpdatePoDto.getPoNumber()) {
            description += (changeCount + 1) + ". PO Number updated from '" + po.getPoNumber() + "' to '" + addUpdatePoDto.getPoNumber() + "'\n";
            changeCount++;
        }
        if (!formattedCreationDate.equals(String.valueOf(addUpdatePoDto.getCreationDate()))) {
            description += (changeCount + 1) + ". Creation Date updated from '" + po.getCreationDate() + "' to '" + addUpdatePoDto.getCreationDate() + "'\n";
            changeCount++;
        }
        if (!formattedPoCreationDate.equals(String.valueOf(addUpdatePoDto.getPoCreationDate()))) {
            description += (changeCount + 1) + ". PO Creation Date updated from '" + po.getPoCreationDate() + "' to '" + addUpdatePoDto.getPoCreationDate() + "'\n";
            changeCount++;
        }
        if (!po.getPoType().equals(addUpdatePoDto.getPoType())) {
            description += (changeCount + 1) + ". PO Type updated from '" + po.getPoType() + "' to '" + addUpdatePoDto.getPoType() + "'\n";
            changeCount++;
        }
        if (!po.getVendorName().equals(addUpdatePoDto.getVendorName())) {
            description += (changeCount + 1) + ". Vendor Name updated from '" + po.getVendorName() + "' to '" + addUpdatePoDto.getVendorName() + "'\n";
            changeCount++;
        }
        if (!po.getVendorSiteCode().equals(addUpdatePoDto.getVendorSiteCode())) {
            description += (changeCount + 1) + ". Vendor Site Code updated from '" + po.getVendorSiteCode() + "' to '" + addUpdatePoDto.getVendorSiteCode() + "'\n";
            changeCount++;
        }
        if (!po.getPoDescription().equals(addUpdatePoDto.getPoDescription())) {
            description += (changeCount + 1) + ". PO Description updated from '" + po.getPoDescription() + "' to '" + addUpdatePoDto.getPoDescription() + "'\n";
            changeCount++;
        }
        if (!po.getApprovalStatus().equals(addUpdatePoDto.getApprovalStatus())) {
            description += (changeCount + 1) + ". Approval Status updated from '" + po.getApprovalStatus() + "' to '" + addUpdatePoDto.getApprovalStatus() + "'\n";
            changeCount++;
        }
        if (!po.getCurrency().equals(addUpdatePoDto.getCurrency())) {
            description += (changeCount + 1) + ". Currency updated from '" + po.getCurrency() + "' to '" + addUpdatePoDto.getCurrency() + "'\n";
            changeCount++;
        }
        if (po.getAmount().compareTo(addUpdatePoDto.getAmount()) != 0) {
            description += (changeCount + 1) + ". Amount updated from '" + po.getAmount() + "' to '" + addUpdatePoDto.getAmount() + "'\n";
            changeCount++;
        }
        if (po.getMatchedAmount().compareTo(addUpdatePoDto.getMatchedAmount()) != 0) {
            description += (changeCount + 1) + ". Matched Amount updated from '" + po.getMatchedAmount() + "' to '" + addUpdatePoDto.getMatchedAmount() + "'\n";
            changeCount++;
        }
        if (!po.getBuyerName().equals(addUpdatePoDto.getBuyerName())) {
            description += (changeCount + 1) + ". Buyer Name updated from '" + po.getBuyerName() + "' to '" + addUpdatePoDto.getBuyerName() + "'\n";
            changeCount++;
        }
        if (!po.getClosureStatus().equals(addUpdatePoDto.getClosureStatus())) {
            description += (changeCount + 1) + ". Closure Status updated from '" + po.getClosureStatus() + "' to '" + addUpdatePoDto.getClosureStatus() + "'\n";
            changeCount++;
        }
        if (!po.getPrNumber().equals(addUpdatePoDto.getPrNumber())) {
            description += (changeCount + 1) + ". PR Number updated from '" + po.getPrNumber() + "' to '" + addUpdatePoDto.getPrNumber() + "'\n";
            changeCount++;
        }
        if (!formattedPrCreationDate.equals(String.valueOf(addUpdatePoDto.getPrCreationDate()))) {
            description += (changeCount + 1) + ". PR Creation Date updated from '" + po.getPrCreationDate() + "' to '" + addUpdatePoDto.getPrCreationDate() + "'\n";
            changeCount++;
        }
        if (!po.getRequisitionHeaderId().equals(addUpdatePoDto.getRequisitionHeaderId())) {
            description += (changeCount + 1) + ". Requisition Header ID updated from '" + po.getRequisitionHeaderId() + "' to '" + addUpdatePoDto.getRequisitionHeaderId() + "'\n";
            changeCount++;
        }
        if (!po.getRequesterName().equals(addUpdatePoDto.getRequesterName())) {
            description += (changeCount + 1) + ". Requester Name updated from '" + po.getRequesterName() + "' to '" + addUpdatePoDto.getRequesterName() + "'\n";
            changeCount++;
        }
        if (!po.getRequesterEmpNum().equals(addUpdatePoDto.getRequesterEmpNum())) {
            description += (changeCount + 1) + ". Requester Employee Number updated from '" + po.getRequesterEmpNum() + "' to '" + addUpdatePoDto.getRequesterEmpNum() + "'\n";
            changeCount++;
        }
        if (!po.getLineNum().equals(addUpdatePoDto.getLineNum())) {
            description += (changeCount + 1) + ". Line Number updated from '" + po.getLineNum() + "' to '" + addUpdatePoDto.getLineNum() + "'\n";
            changeCount++;
        }
        if (!po.getItemCode().equals(addUpdatePoDto.getItemCode())) {
            description += (changeCount + 1) + ". Item Code updated from '" + po.getItemCode() + "' to '" + addUpdatePoDto.getItemCode() + "'\n";
            changeCount++;
        }
        if (!po.getItemDescription().equals(addUpdatePoDto.getItemDescription())) {
            description += (changeCount + 1) + ". Item Description updated from '" + po.getItemDescription() + "' to '" + addUpdatePoDto.getItemDescription() + "'\n";
            changeCount++;
        }
        if (!po.getLineItemDescription().equals(addUpdatePoDto.getLineItemDescription())) {
            description += (changeCount + 1) + ". Line Item Description updated from '" + po.getLineItemDescription() + "' to '" + addUpdatePoDto.getLineItemDescription() + "'\n";
            changeCount++;
        }
        if (!po.getUnit().equals(addUpdatePoDto.getUnit())) {
            description += (changeCount + 1) + ". Unit updated from '" + po.getUnit() + "' to '" + addUpdatePoDto.getUnit() + "'\n";
            changeCount++;
        }
        if (po.getUnitPrice().compareTo(addUpdatePoDto.getUnitPrice()) != 0) {
            description += (changeCount + 1) + ". Unit Price updated from '" + po.getUnitPrice() + "' to '" + addUpdatePoDto.getUnitPrice() + "'\n";
            changeCount++;
        }
        if (!po.getQuantity().equals(addUpdatePoDto.getQuantity())) {
            description += (changeCount + 1) + ". Quantity updated from '" + po.getQuantity() + "' to '" + addUpdatePoDto.getQuantity() + "'\n";
            changeCount++;
        }
        if (po.getLineAmount().compareTo(addUpdatePoDto.getLineAmount()) != 0) {
            description += (changeCount + 1) + ". Line Amount updated from '" + po.getLineAmount() + "' to '" + addUpdatePoDto.getLineAmount() + "'\n";
            changeCount++;
        }
        if (!po.getBudgetAccount().equals(addUpdatePoDto.getBudgetAccount())) {
            description += (changeCount + 1) + ". Budget Account updated from '" + po.getBudgetAccount() + "' to '" + addUpdatePoDto.getBudgetAccount() + "'\n";
            changeCount++;
        }
        if (!po.getSegment6Desc().equals(addUpdatePoDto.getSegment6Desc())) {
            description += (changeCount + 1) + ". Segment 6 Description updated from '" + po.getSegment6Desc() + "' to '" + addUpdatePoDto.getSegment6Desc() + "'\n";
            changeCount++;
        }
        if (!po.getPurchaseDeliverToPersonId().equals(addUpdatePoDto.getPurchaseDeliverToPersonId())) {
            description += (changeCount + 1) + ". Purchase Deliver To Person ID updated from '" + po.getPurchaseDeliverToPersonId() + "' to '" + addUpdatePoDto.getPurchaseDeliverToPersonId() + "'\n";
            changeCount++;
        }
        if (!formattedPurchasePoDate.equals(String.valueOf(addUpdatePoDto.getPurchasePoDate()))) {
            description += (changeCount + 1) + ". Purchase PO Date updated from '" + po.getPurchasePoDate() + "' to '" + addUpdatePoDto.getPurchasePoDate() + "'\n";
            changeCount++;
        }
        if(!po.getDepartment().equals(addUpdatePoDto.getDepartment())){
            description += (changeCount + 1) + ". Department updated from '" + po.getDepartment() + "' to '" + addUpdatePoDto.getDepartment() + "'\n";
            changeCount++;
        }
        if(addUpdatePoDto.getPoFile() != null){
            description += (changeCount + 1) + ". File Changed'\n";
            changeCount++;
        }
        if (changeCount == 0){
            return "no Changes";
        }else{
            return description;
        }
    }

}
