package com.mobitel.data_management.dto.responseDto;

import lombok.Data;

@Data
public class ViewSingleUserActivityAmcDto {
    private String version;
    private String action;
    private String description;
    private String user;
    private String rowBefore;
    private String rowAfter;
    private String dateTime;
}
