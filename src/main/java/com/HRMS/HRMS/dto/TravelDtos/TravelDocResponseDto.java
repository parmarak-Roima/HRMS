package com.HRMS.HRMS.dto.TravelDtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TravelDocResponseDto {
    private Long id;
    private Long travelId;

    private String docType; // PASSPORT, VISA, etc.
    private String fileUrl;

    // Who uploaded it? (Could be HR or Employee)
    private Long uploadedById;
    private String uploadedByName;

    // Who does it belong to? (Null = Shared/General)
    private Long ownerId;
    private String ownerName;

    private LocalDateTime uploadedAt;
}
