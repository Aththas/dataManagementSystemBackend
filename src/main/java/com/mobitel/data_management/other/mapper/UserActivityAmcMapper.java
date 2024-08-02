package com.mobitel.data_management.other.mapper;

import com.mobitel.data_management.dto.responseDto.ViewSingleUserActivityAmcDto;
import com.mobitel.data_management.dto.responseDto.ViewUserActivityAmcDto;
import com.mobitel.data_management.entity.UserActivityAmc;
import org.springframework.stereotype.Component;

@Component
public class UserActivityAmcMapper {
    public ViewUserActivityAmcDto userActivityViewMapper(UserActivityAmc userActivityAmc) {
        ViewUserActivityAmcDto viewUserActivityAmcDto = new ViewUserActivityAmcDto();
        viewUserActivityAmcDto.setId(userActivityAmc.getId());
        viewUserActivityAmcDto.setUser(userActivityAmc.getUser().getUsername());
        viewUserActivityAmcDto.setAction(userActivityAmc.getAction());
        viewUserActivityAmcDto.setVersion(userActivityAmc.getVersion());
        viewUserActivityAmcDto.setAfterFile(userActivityAmc.getAfterFile());
        viewUserActivityAmcDto.setBeforeFile(userActivityAmc.getBeforeFile());
        viewUserActivityAmcDto.setDateTime(userActivityAmc.getDateTime());
        return viewUserActivityAmcDto;
    }

    public ViewSingleUserActivityAmcDto userSingleActivityViewMapper(UserActivityAmc userActivityAmc) {
        ViewSingleUserActivityAmcDto viewUserActivityAmcDto = new ViewSingleUserActivityAmcDto();
        viewUserActivityAmcDto.setUser(userActivityAmc.getUser().getUsername());
        viewUserActivityAmcDto.setAction(userActivityAmc.getAction());
        viewUserActivityAmcDto.setVersion(userActivityAmc.getVersion());
        viewUserActivityAmcDto.setDescription(userActivityAmc.getDescription());
        viewUserActivityAmcDto.setAfterFile(userActivityAmc.getAfterFile());
        viewUserActivityAmcDto.setBeforeFile(userActivityAmc.getBeforeFile());
        viewUserActivityAmcDto.setRowBefore(userActivityAmc.getRowBefore());
        viewUserActivityAmcDto.setRowAfter(userActivityAmc.getRowAfter());
        viewUserActivityAmcDto.setDateTime(userActivityAmc.getDateTime());
        return viewUserActivityAmcDto;
    }
}
