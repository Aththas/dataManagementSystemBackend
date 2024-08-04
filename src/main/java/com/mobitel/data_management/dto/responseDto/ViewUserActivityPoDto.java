package com.mobitel.data_management.dto.responseDto;

import lombok.Data;

@Data
public class ViewUserActivityPoDto {
    private Integer id;
    private String version;
    private String action;
    private String beforeFile;
    private String afterFile;
    private String user;
    private String dateTime;
}
