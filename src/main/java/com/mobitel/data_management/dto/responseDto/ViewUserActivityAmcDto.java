package com.mobitel.data_management.dto.responseDto;

import lombok.Data;

@Data
public class ViewUserActivityAmcDto {
    private String version;
    private String action;
    private String description;
    private String beforeFile;
    private String afterFile;
    private String user;
}
