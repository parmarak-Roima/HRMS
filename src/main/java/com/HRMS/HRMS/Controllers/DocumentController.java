package com.HRMS.HRMS.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.HRMS.HRMS.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/documents")
public class DocumentController {


    private final DocumentService documentService;

    @Autowired
    public DocumentController( DocumentService documentService ){
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDoc(
            @RequestBody MultipartFile file,
            @RequestParam("module") String module,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "isProfile", defaultValue = "false") boolean isProfile) {

        String fileUrl = documentService.uploadFile(file, module, userId, isProfile);

        return ResponseEntity.ok("File uploaded successfully. URL: " + fileUrl);
    }

}
