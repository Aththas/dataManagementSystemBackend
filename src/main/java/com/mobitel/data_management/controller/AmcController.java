package com.mobitel.data_management.controller;

import com.mobitel.data_management.dto.requestDto.AddUpdateAmcDto;
import com.mobitel.data_management.service.AmcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/amc")
@RequiredArgsConstructor
public class AmcController {
    private final AmcService amcService;

    @PostMapping("/addAmc")
    public ResponseEntity<String> addAmc(@RequestBody AddUpdateAmcDto addUpdateAmcDto){
        return amcService.addAmc(addUpdateAmcDto);
    }

    @PutMapping("/updateMyAmc")
    public ResponseEntity<String> updateAmc(@RequestParam Integer id, @RequestBody AddUpdateAmcDto addUpdateAmcDto){
        return amcService.updateAmc(id, addUpdateAmcDto);
    }

    @GetMapping("/viewAmc")
    public ResponseEntity<?> viewAmc(@RequestParam Integer id){ //admin only
        return amcService.viewAmc(id);
    }

    @GetMapping("/viewAllAmcList")//with sorting and pagination
    public ResponseEntity<?> viewAllAmc(@RequestParam int page, @RequestParam int size,
                                        @RequestParam String sortBy, @RequestParam boolean ascending){ //admin only
        return amcService.viewAllAmc(page, size, sortBy, ascending);
    }

    @GetMapping("/viewMyAmc")
    public ResponseEntity<?> viewMyAmc(@RequestParam Integer id){
        return amcService.viewMyAmc(id);
    }

    @GetMapping("/viewAllMyAmcList")//with sorting and pagination
    public ResponseEntity<?> viewAllMyAmc(@RequestParam int page, @RequestParam int size,
                                          @RequestParam String sortBy, @RequestParam boolean ascending){
        return amcService.viewAllMyAmc(page, size, sortBy, ascending);
    }

    @DeleteMapping("/deleteMyAmc")
    public ResponseEntity<?> deleteAmc(@RequestParam Integer id){
        return amcService.deleteMyAmc(id);
    }
}
