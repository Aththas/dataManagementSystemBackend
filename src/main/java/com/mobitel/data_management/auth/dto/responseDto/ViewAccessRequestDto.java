package com.mobitel.data_management.auth.dto.responseDto;

import lombok.Data;

@Data
public class ViewAccessRequestDto {
    private Integer id;
    private String reason;
    private Integer grpOwnerId;
    private String grpName;
    private Integer requesterId;
    private String status;
    private String requesterEmail;
}
