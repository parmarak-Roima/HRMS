package com.HRMS.HRMS.service;

import com.HRMS.HRMS.exception.BadRequestException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class DocumentService {

    private final Cloudinary cloudinary;

    @Autowired
    public DocumentService( Cloudinary cloudinary ){
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file, String moduleName, Long userId, boolean isProfilePic) {
        try {
            if ( file == null || file.isEmpty()) {
                throw new BadRequestException("Cannot upload empty file");
            }
            long size = file.getSize(); // in bytes
            String contentType = file.getContentType();
            if (contentType == null ||
                    !(contentType.equalsIgnoreCase("image/jpeg") ||
                            contentType.equalsIgnoreCase("image/png") ||
                            contentType.equalsIgnoreCase("application/pdf"))) {
                throw new IllegalArgumentException("Invalid file content type.");
            }
            if( size>500000){
              throw new BadRequestException("file size should be less then 500 kb");
            }
            //folder
            String folderPath = "hrms/" + moduleName;

            String publicId;
            String originalFilename = file.getOriginalFilename();

            if (isProfilePic) {
                publicId = "avatar";
            } else {
                //Replace all whitespace (spaces, tabs, newlines) with a single underscore
                String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                //check if file has dot
                if (sanitized.contains(".")) {
                    //remove all from the last dot to end of the string
                    sanitized = sanitized.substring(0, sanitized.lastIndexOf('.'));
                }
                publicId = System.currentTimeMillis() + "_" + sanitized;
                publicId += "/user_" + userId;
            }

            Map params = ObjectUtils.asMap(
                    "folder", folderPath,
                    "public_id", publicId,
                    "resource_type", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage());
        }
    }

}
