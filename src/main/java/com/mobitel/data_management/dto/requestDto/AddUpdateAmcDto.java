package com.mobitel.data_management.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class AddUpdateAmcDto {

    @NotEmpty(message = "Empty Contract Name")
    @NotNull(message = "Invalid Contract Name")
    private String contractName;

    @NotEmpty(message = "Empty Existing Partner")
    @NotNull(message = "Invalid Existing Partner")
    private String existingPartner;

    @NotEmpty(message = "Empty User Division")
    @NotNull(message = "Invalid User Division")
    private String userDivision;

    @Positive(message = "Initial cost in USD must be a positive number")
    @NotNull(message = "Invalid Initial Cost USD")
    private long initialCostUSD;

    @Positive(message = "Initial cost in LKR must be a positive number")
    @NotNull(message = "Invalid Initial Cost LKR")
    private long initialCostLKR;

    @NotNull(message = "Invalid Start Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @NotNull(message = "Invalid End Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Positive(message = "AMC Value USD must be a positive number")
    @NotNull(message = "Invalid AMC Value USD")
    private long amcValueUSD;

    @Positive(message = "AMC Value LKR must be a positive number")
    @NotNull(message = "Invalid AMC Value LKR")
    private long amcValueLKR;

    @Positive(message = "AMC Percentage Upon Purchase Price must be a positive number")
    @NotNull(message = "Invalid AMC Percentage Upon Purchase Price")
    private long amcPercentageUponPurchasePrice;

    @NotEmpty(message = "Empty Category")
    @NotNull(message = "Invalid Category")
    private String category;

    private MultipartFile amcFile;
}
