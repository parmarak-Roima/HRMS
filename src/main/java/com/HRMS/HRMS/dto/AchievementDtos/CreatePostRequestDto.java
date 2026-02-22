package com.HRMS.HRMS.dto.AchievementDtos;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequestDto {
    private String title;
    private String description;
    private List<String> tags;
    private List<MultipartFile> files; // uploaded by user, stored to Cloudinary in service
}