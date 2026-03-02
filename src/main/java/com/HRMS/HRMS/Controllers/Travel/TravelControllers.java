package com.HRMS.HRMS.Controllers.Travel;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.TravelDtos.CreateTravelDto;
import com.HRMS.HRMS.dto.TravelDtos.ShowTravelDto;
import com.HRMS.HRMS.service.Travel.TravelService;
import com.HRMS.HRMS.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel")
public class TravelControllers {

    private final TravelService travelService ;

    @Autowired
    public TravelControllers(TravelService travelService){
        this.travelService = travelService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<CreateTravelDto>> createTravel(@Valid @RequestBody CreateTravelDto createTravelDto) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CreateTravelDto createdTravel = travelService.createTravel(createTravelDto,user);
        return new ResponseEntity<>(
                new ApiResponse<>("Travel Plan created successfully", createdTravel),
                HttpStatus.CREATED
        );
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<ApiResponse<List<ShowTravelDto>>> getAllTravel() {
        return new ResponseEntity<>(
                new ApiResponse<>("all travels", travelService.getAllTravels()),
                HttpStatus.OK
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ShowTravelDto>> getTravelById(@PathVariable Long id) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(
                new ApiResponse<>("travel_by_id", travelService.getTravelById(id,user)),
                HttpStatus.OK
        );
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<ShowTravelDto>> updateTravel(@PathVariable Long id , @RequestBody CreateTravelDto createTravelDto ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(
                new ApiResponse<>("travel updated successfully!", travelService.updateTravel(id,createTravelDto,user)),
                HttpStatus.OK
        );
    }

    @GetMapping("HR")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<List<ShowTravelDto>>> getTravelForHr() {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(
                new ApiResponse<>("travel_for_hr", travelService.getTravelForHr(user)),
                HttpStatus.OK
        );
    }


    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus() {
        travelService.markAsCompletedTravel();
        return new ResponseEntity<>(
                new ApiResponse<>("travel_for_hr",null ),
                HttpStatus.OK
        );
    }

}
