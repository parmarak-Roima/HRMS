package com.HRMS.HRMS.Controllers.Config;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.ConfigDto;
import com.HRMS.HRMS.entity.Config.Config;
import com.HRMS.HRMS.service.Config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigController {

    public final ConfigService configService;

    @Autowired
    public ConfigController(
            ConfigService configService
    ){
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Config>>> allConfiguration(){
        return new ResponseEntity<>(
                new ApiResponse<>("All configuration fetched",
                     configService.allConfiguration()   ),
                HttpStatus.OK
        );
    }

    @GetMapping("/keys")
    public ResponseEntity<ApiResponse<List<String>>> getAllKey(){
        return new ResponseEntity<>(
                new ApiResponse<>("All keys fetched",
                        configService.allKey()   ),
                HttpStatus.OK
        );
    }
    @PatchMapping("/{configId}")
    public ResponseEntity<ApiResponse<Void>> updateConfig(
            @RequestBody ConfigDto configDto,
            @PathVariable Long configId
    ){
        configService.UpdateConfig(configId,configDto);
        return new ResponseEntity<>(
                new ApiResponse<>("Config updated successFully!!",null),
                HttpStatus.OK
        );
    }

}
