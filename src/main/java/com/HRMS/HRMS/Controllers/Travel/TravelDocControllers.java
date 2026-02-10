package com.HRMS.HRMS.Controllers.Travel;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.CustomUserPrincipal;
import com.HRMS.HRMS.dto.TravelDtos.CreateTravelDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelDocCreateDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelDocResponseDto;
import com.HRMS.HRMS.service.Travel.TravelDocService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/travelDoc")
public class TravelDocControllers {
    TravelDocService travelDocService;

    @Autowired
    public TravelDocControllers(TravelDocService travelDocService){
        this.travelDocService = travelDocService;
    }

    //hr and employee for that travel
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<TravelDocResponseDto>> createTravelDoc(@ModelAttribute @Valid TravelDocCreateDto travelDocCreateDto){
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TravelDocResponseDto travelDocResponseDto = travelDocService.uploadDocument(travelDocCreateDto,user);
        return new ResponseEntity<>(
                new ApiResponse<>("Travel document uploaded successfully", travelDocResponseDto),
                HttpStatus.CREATED
        );
    }
    //hr only for that travel
    @GetMapping("/{travelId}/all")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TravelDocResponseDto>>> getAllDocs(@PathVariable Long travelId) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TravelDocResponseDto> docs = travelDocService.getAllDocsForTravel(travelId,user);
        return ResponseEntity.ok(new ApiResponse<>("Fetched all documents", docs));
    }

    @GetMapping("/{travelId}/employee/{empId}")
    public ResponseEntity<ApiResponse<List<TravelDocResponseDto>>> getEmployeeDocs(
            @PathVariable Long travelId,
            @PathVariable Long empId) {
        List<TravelDocResponseDto> docs = travelDocService.getDocsForEmployee(travelId, empId);
        return ResponseEntity.ok(new ApiResponse<>("Fetched employee documents", docs));
    }

    //employee for that travel
    @GetMapping("/{travelId}/general")
    public ResponseEntity<ApiResponse<List<TravelDocResponseDto>>> getGeneralDocs(@PathVariable Long travelId) {
        List<TravelDocResponseDto> docs = travelDocService.getGeneralDocs(travelId);
        return ResponseEntity.ok(new ApiResponse<>("Fetched general documents", docs));
    }

   //user who uploaded it
    @PatchMapping(value = "/{docId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<TravelDocResponseDto>> updateDocument(
            @PathVariable Long docId,
            @ModelAttribute MultipartFile newFile) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TravelDocResponseDto response = travelDocService.updateDocumentFile(docId, newFile,user);
        return ResponseEntity.ok(new ApiResponse<>("Document file updated successfully", response));
    }

    //only user who uploaded it
    @DeleteMapping("/{docId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long docId) {
        travelDocService.deleteDocument(docId);
        return ResponseEntity.ok(new ApiResponse<>("Document deleted successfully",null));
    }
}
