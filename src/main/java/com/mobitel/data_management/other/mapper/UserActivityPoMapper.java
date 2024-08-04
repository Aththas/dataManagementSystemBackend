package com.mobitel.data_management.other.mapper;

import com.mobitel.data_management.dto.responseDto.ViewSingleUserActivityAmcDto;
import com.mobitel.data_management.dto.responseDto.ViewSingleUserActivityPoDto;
import com.mobitel.data_management.dto.responseDto.ViewUserActivityAmcDto;
import com.mobitel.data_management.dto.responseDto.ViewUserActivityPoDto;
import com.mobitel.data_management.entity.UserActivityAmc;
import com.mobitel.data_management.entity.UserActivityPo;
import org.springframework.stereotype.Component;

@Component
public class UserActivityPoMapper {

    public ViewUserActivityPoDto userActivityViewMapper(UserActivityPo userActivityPo) {
        ViewUserActivityPoDto viewUserActivityPoDto = new ViewUserActivityPoDto();
        viewUserActivityPoDto.setId(userActivityPo.getId());
        viewUserActivityPoDto.setUser(userActivityPo.getUser().getUsername());
        viewUserActivityPoDto.setAction(userActivityPo.getAction());
        viewUserActivityPoDto.setVersion(userActivityPo.getVersion());
        viewUserActivityPoDto.setAfterFile(userActivityPo.getAfterFile());
        viewUserActivityPoDto.setBeforeFile(userActivityPo.getBeforeFile());
        viewUserActivityPoDto.setDateTime(userActivityPo.getDateTime());
        return viewUserActivityPoDto;
    }

    public ViewSingleUserActivityPoDto userSingleActivityViewMapper(UserActivityPo userActivityPo) {
        ViewSingleUserActivityPoDto viewUserActivityPoDto = new ViewSingleUserActivityPoDto();
        viewUserActivityPoDto.setUser(userActivityPo.getUser().getUsername());
        viewUserActivityPoDto.setAction(userActivityPo.getAction());
        viewUserActivityPoDto.setVersion(userActivityPo.getVersion());
        viewUserActivityPoDto.setDescription(userActivityPo.getDescription());
        viewUserActivityPoDto.setRowBefore(userActivityPo.getRowBefore());
        viewUserActivityPoDto.setRowAfter(userActivityPo.getRowAfter());
        viewUserActivityPoDto.setDateTime(userActivityPo.getDateTime());
        return viewUserActivityPoDto;
    }
}
