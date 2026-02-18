package com.HRMS.HRMS.service.Game;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.GameDtos.BookingRequestCreateDto;
import com.HRMS.HRMS.dto.GameDtos.BookingRequestResponseDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.GameEntities.BookingParticipant;
import com.HRMS.HRMS.entity.GameEntities.BookingRequest;
import com.HRMS.HRMS.entity.GameEntities.GameSlot;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.Game.BookingParticipantRepository;
import com.HRMS.HRMS.repository.Game.BookingRequestRepository;
import com.HRMS.HRMS.repository.Game.GameSlotsRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class BookingRequestService {

    private final BookingParticipantRepository bookingParticipantRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final GameSlotsRepository gameSlotsRepository;
    private final EmployeeRepository employeeRepository;

    public BookingRequestService(
            BookingRequestRepository bookingRequestRepository,
            BookingParticipantRepository bookingParticipantRepository,
            GameSlotsRepository gameSlotsRepository, EmployeeRepository employeeRepository){
        this.bookingParticipantRepository = bookingParticipantRepository;
        this.bookingRequestRepository = bookingRequestRepository;
        this.gameSlotsRepository = gameSlotsRepository;
        this.employeeRepository = employeeRepository;
    }

    public void createBookingRequest(BookingRequestCreateDto dto, CustomUserPrincipal user){
        BookingRequest bookingRequest = new BookingRequest();
        //validation
        GameSlot gameSlot = gameSlotsRepository.findById(dto.getSlotId()).orElseThrow(
                () ->     new ResourceNotFoundException("slot not found!!")
        );
        Employee employee = employeeRepository.findById(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("primary booker not found!!")
        );
        //create booking request
        bookingRequest.setSlot(gameSlot);
        bookingRequest.setStatus(BookingRequest.RequestStatus.PENDING);
        bookingRequest.setPrimaryBooker(employee);
        bookingRequestRepository.save(bookingRequest);
        //add primary booker in participation
        BookingParticipant primaryBookerParticipation = new BookingParticipant();
        primaryBookerParticipation.setEmployee(employee);
        primaryBookerParticipation.setBookingRequest(bookingRequest);
        bookingParticipantRepository.save(primaryBookerParticipation);
        //add participation there
        dto.getParticipantsId().forEach(
                id -> {
                    Employee participant =  employeeRepository.findById(id).orElseThrow(
                            () ->     new ResourceNotFoundException("participant not found !!")
                    );
                    BookingParticipant bookingParticipant = new BookingParticipant();
                    bookingParticipant.setEmployee(participant);
                    bookingParticipant.setBookingRequest(bookingRequest);
                    bookingParticipantRepository.save(bookingParticipant);
                }
        );
    }

    public List<BookingRequestResponseDto> bookingRequestForSlot( Long slotId ){
        List<BookingRequest> bookingRequests = bookingRequestRepository.findBookingRequestBySlot_Id(slotId);
       return bookingRequests.stream().map(
                bookingRequest -> {
                    return new BookingRequestResponseDto(
                            bookingRequest.getId(),
                            bookingRequest.getStatus().toString(),
                            bookingRequest.getSlot().getId(),
                            bookingRequest.getRequestedAt(),
                            bookingRequest.getPrimaryBooker().getId(),
                            bookingRequest.getParticipants().stream().map(
                                    BookingParticipant::getId
                            ).toList()
                    );
                }
        ).toList();
    }
}
