package com.mobitel.data_management.controller;

import com.mobitel.data_management.dto.requestDto.FileAccessDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/file")
public class DemoController {

    @PostMapping
    public String a(@RequestBody FileAccessDto fileAccessDto){

        return "redirect:"+fileAccessDto.getFilePath();
    }
}
