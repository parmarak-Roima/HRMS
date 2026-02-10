package com.HRMS.HRMS.dto.TravelDtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class TravelDocCreateDto {

    @NotNull(message = "Travel ID is required")
    private Long travelId;

    private Long ownerId;

    @NotBlank(message = "Document type is required")
    private String docTypeStr;

    @NotNull(message = "File is required")
    private MultipartFile file;
}

