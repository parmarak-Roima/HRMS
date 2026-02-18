package com.HRMS.HRMS.Controllers.Game;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.GameDtos.BookingRequestCreateDto;
import com.HRMS.HRMS.service.Game.BookingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game/booking")
public class BookingController {

    private final BookingRequestService bookingRequestService;

    @Autowired
    public BookingController(
            BookingRequestService bookingRequestService
    ){
        this.bookingRequestService = bookingRequestService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBookingRequest(
            @RequestBody BookingRequestCreateDto bookingRequestCreateDto
            ){
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookingRequestService.createBookingRequest(bookingRequestCreateDto,user);
        return  new ResponseEntity<>(
                new ApiResponse<>(
                        "booking request created successFully!!", null
                ),
                HttpStatus.CREATED
        );
    }
}
