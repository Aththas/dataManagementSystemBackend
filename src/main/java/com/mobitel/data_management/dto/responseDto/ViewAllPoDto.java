package com.mobitel.data_management.dto.responseDto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ViewAllPoDto {
    private Integer id;

    private long poNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creationDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date poCreationDate;
    private String poType;
    private String vendorName;
    private String approvalStatus;
    private String department;
    private String poFile;
    private String user;
}
