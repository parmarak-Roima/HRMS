package com.HRMS.HRMS.Controllers.Game;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.GameDtos.BookingRequestCreateDto;
import com.HRMS.HRMS.dto.GameDtos.BookingRequestResponseDto;
import com.HRMS.HRMS.service.Game.BookingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<BookingRequestResponseDto>> createBookingRequest(
            @RequestBody BookingRequestCreateDto bookingRequestCreateDto
            ){
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  new ResponseEntity<>(
                new ApiResponse<>(
                        "booking request created successFully!!", bookingRequestService.createBookingRequest(bookingRequestCreateDto,user)
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<BookingRequestResponseDto>>> getMyHistory() {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched booking history", bookingRequestService.getMyBookingHistory(user)),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable Long bookingId) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        bookingRequestService.cancelBooking(bookingId, user);

        return new ResponseEntity<>(
                new ApiResponse<>("Booking cancelled successfully", null),
                HttpStatus.OK
        );
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<ApiResponse<Long>> getWinningBrId(@PathVariable Long slotId) {
        return new ResponseEntity<>(
                new ApiResponse<>("Winner fetched SuccessFully", bookingRequestService.getWinningBookingRequestId(slotId)),
                HttpStatus.OK
        );
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<Void>> assignToPrior() {
        bookingRequestService.assignSlotToMostPrior();
        return new ResponseEntity<>(
                new ApiResponse<>("Assigned successFully", null),
                HttpStatus.OK
        );
    }

}
