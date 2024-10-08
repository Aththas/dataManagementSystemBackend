package com.mobitel.data_management.dto.responseDto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ViewAmcDto {
    private String contractName;
    private String existingPartner;
    private String userDivision;
    private long initialCostUSD;
    private long initialCostLKR;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private long amcValueUSD;
    private long amcValueLKR;
    private long amcPercentageUponPurchasePrice;
    private String category;
    private String user;
}

