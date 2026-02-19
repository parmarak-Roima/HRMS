package com.HRMS.HRMS.service.Game;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.dto.GameDtos.BookingRequestCreateDto;
import com.HRMS.HRMS.dto.GameDtos.BookingRequestResponseDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.GameEntities.*;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.Game.BookingParticipantRepository;
import com.HRMS.HRMS.repository.Game.BookingRequestRepository;
import com.HRMS.HRMS.repository.Game.GameInterestRepository;
import com.HRMS.HRMS.repository.Game.GameSlotsRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@Slf4j
public class BookingRequestService {

    private final BookingParticipantRepository bookingParticipantRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final GameSlotsRepository gameSlotsRepository;
    private final EmployeeRepository employeeRepository;
    private final GameInterestRepository gameInterestRepository;
    private final GameInterestService gameInterestService;

    public BookingRequestService(
            BookingRequestRepository bookingRequestRepository,
            BookingParticipantRepository bookingParticipantRepository,
            GameSlotsRepository gameSlotsRepository, EmployeeRepository employeeRepository, GameInterestRepository gameInterestRepository, GameInterestService gameInterestService){
        this.bookingParticipantRepository = bookingParticipantRepository;
        this.bookingRequestRepository = bookingRequestRepository;
        this.gameSlotsRepository = gameSlotsRepository;
        this.employeeRepository = employeeRepository;
        this.gameInterestRepository = gameInterestRepository;
        this.gameInterestService = gameInterestService;
    }

    @Transactional
    public BookingRequestResponseDto createBookingRequest(BookingRequestCreateDto dto, CustomUserPrincipal user){
        //validation
        GameSlot gameSlot = gameSlotsRepository.findById(dto.getSlotId()).orElseThrow(
                () ->     new ResourceNotFoundException("slot not found!!")
        );
        if(!(gameSlot.getGame().getMinPlayers() <= dto.getParticipantsId().size()
                && gameSlot.getGame().getMaxPlayers() >= dto.getParticipantsId().size())){
            throw new IllegalArgumentException("give player according to game ");
        }
        //employee can make booking request only before 1.5 hour
        if(!gameSlot.getStartTime().minusMinutes(75).isBefore(LocalTime.now())){
            throw new IllegalArgumentException("you can only make booking request before 45 minutes , booking request time is over !! ");
        }
        //check for active booking
        boolean alreadyHasBooking = bookingRequestRepository.existsByPrimaryBooker_IdAndSlot_DateAndStatus(
                user.getId(),
                gameSlot.getDate(),
                BookingRequest.RequestStatus.CONFIRMED
        );

        if (alreadyHasBooking) {
            throw new IllegalArgumentException("Fairness Rule: You already have a booking request for this date!");
        }
        Employee primaryBooker = employeeRepository.findById(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("primary booker not found!!")
        );
        GameInterest interest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(
                gameSlot.getGame().getId(), primaryBooker.getId()
        );
        if (interest == null || !interest.isInterested()) {
            throw new IllegalArgumentException("You must mark 'Interest' in this game profile before booking!");
        }
        int playCount = interest.getPlayedInCurrentCycle();

        //create booking request
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setSlot(gameSlot);
        bookingRequest.setStatus(BookingRequest.RequestStatus.PENDING);
        bookingRequest.setPrimaryBooker(primaryBooker);

        if (playCount == 0 && gameSlot.getStatus().equals(GameSlot.SlotStatus.OPEN) ) {
            bookingRequest.setStatus(BookingRequest.RequestStatus.CONFIRMED);
            gameSlot.setStatus(GameSlot.SlotStatus.BOOKED);
            gameSlotsRepository.save(gameSlot);
            interest.setPlayedInCurrentCycle(playCount+1);
            gameInterestRepository.save(interest);
            log.info("Immediate Booking Confirmed for {}", primaryBooker.getName());
        } else {
            bookingRequest.setStatus(BookingRequest.RequestStatus.PENDING);
            log.info("Booking Queued for {}", primaryBooker.getName());
        }

        BookingRequest savedRequest = bookingRequestRepository.save(bookingRequest);

        //add primary booker in participation
        saveParticipant(savedRequest, primaryBooker);
        //add participation there
        dto.getParticipantsId().forEach(
                id -> {
                    Employee participant =  employeeRepository.findById(id).orElseThrow(
                            () ->     new ResourceNotFoundException("participant not found !!")
                    );
                    GameInterest gameInterest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id( gameSlot.getGame().getId() , participant.getId());
                    if(!gameInterest.isInterested()){
                       throw new IllegalArgumentException("participants is not interested in this game");
                    }
                    saveParticipant(savedRequest,participant);
                    //increase friends count tooo
                    if (savedRequest.getStatus() == BookingRequest.RequestStatus.CONFIRMED) {
                        GameInterest friendInterest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(
                                gameSlot.getGame().getId(), id
                        );
                        if (friendInterest != null){
                            friendInterest.setPlayedInCurrentCycle(friendInterest.getPlayedInCurrentCycle()+1);
                            gameInterestRepository.save(friendInterest);
                        }

                    }
                }
        );
        return new BookingRequestResponseDto(
                savedRequest.getId(),
                savedRequest.getStatus().toString(),
                savedRequest.getSlot().getId(),
                savedRequest.getSlot().getStatus().toString(),
                savedRequest.getSlot().getGame().getName(),
                savedRequest.getSlot().getStartTime(),
                savedRequest.getSlot().getEndTime(),
                savedRequest.getRequestedAt(),
                savedRequest.getPrimaryBooker().getId(),
                savedRequest.getPrimaryBooker().getEmail(),
               null
        );
    }

    public List<BookingRequestResponseDto> bookingRequestForSlot( Long slotId ){
        GameSlot slot = gameSlotsRepository.findById(slotId).orElseThrow(
                () -> new ResourceNotFoundException("slot not exits!!")
        );
       return slot.getBookingRequests().stream().map(
               this::mapToResponse
        ).toList();
    }

    public List<BookingRequestResponseDto> getMyBookingHistory(CustomUserPrincipal user) {
        Employee employee = employeeRepository.findById(user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Employee not found!!"));
        List<BookingRequest> requests = employee.getBookingParticipants().stream().map(
                BookingParticipant::getBookingRequest
        ).toList();
        return requests.stream().map(this::mapToResponse).toList();
    }

    public void cancelBooking(Long bookingId, CustomUserPrincipal user) {
        BookingRequest request = bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if(!request.getSlot().getStartTime().minusMinutes(75).isBefore(LocalTime.now())){
            throw new IllegalArgumentException("You can not cancel now because slot star time is less then 1.5 hour from now !");
        }
        //Only the Booker can cancel
        if (!request.getPrimaryBooker().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only cancel your own bookings!");
        }
        if (request.getStatus() == BookingRequest.RequestStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled.");
        }
        if (request.getStatus() == BookingRequest.RequestStatus.CONFIRMED) {
            GameSlot slot = request.getSlot();
            slot.setStatus(GameSlot.SlotStatus.OPEN);
            gameSlotsRepository.save(slot);
            decrementPlayCount(request.getPrimaryBooker(), slot.getGame());
        }
        request.setStatus(BookingRequest.RequestStatus.CANCELLED);
        bookingRequestRepository.save(request);
    }

    private void decrementPlayCount(Employee emp, Game game) {
        GameInterest interest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(
                game.getId(), emp.getId()
        );
        if (interest != null && interest.getPlayedInCurrentCycle() > 0) {
            interest.setPlayedInCurrentCycle(interest.getPlayedInCurrentCycle() - 1);
            gameInterestRepository.save(interest);
        }
    }

    public Long getWinningBookingRequestId(Long slotId) {

        GameSlot slot = gameSlotsRepository.findById(slotId).orElseThrow(
                () -> new ResourceNotFoundException("slot not exits!!")
        );

        List<BookingRequest>  pendingRequests = slot.getBookingRequests().stream().filter(bookingRequest ->
                    bookingRequest.getStatus() == BookingRequest.RequestStatus.PENDING

        ).toList();

        if (pendingRequests.isEmpty()) {
            return null;
        }

        BookingRequest winner = pendingRequests.stream()
                .min(Comparator
                        .comparingInt((BookingRequest req) -> getPlayCount(req.getPrimaryBooker().getId(), req.getSlot().getGame().getId()))
                        .thenComparing(BookingRequest::getRequestedAt)
                )
                .orElse(null);

        return winner.getId();
    }

    private int getPlayCount(Long employeeId, Long gameId) {
        GameInterest interest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(gameId, employeeId);
        if (interest != null) {
            return interest.getPlayedInCurrentCycle();
        }
        return 999;
    }

    private void saveParticipant(BookingRequest req, Employee emp) {
        BookingParticipant part = new BookingParticipant();
        part.setBookingRequest(req);
        part.setEmployee(emp);
        bookingParticipantRepository.save(part);
    }

    private BookingRequestResponseDto mapToResponse(BookingRequest req) {

        return new BookingRequestResponseDto(
                req.getId(),
                req.getStatus().toString(),
                req.getSlot().getId(),
                req.getSlot().getStatus().toString(),
                req.getSlot().getGame().getName(),
                req.getSlot().getStartTime(),
                req.getSlot().getEndTime(),
                req.getRequestedAt(),
                req.getPrimaryBooker().getId(),
                req.getPrimaryBooker().getEmail(),
                req.getParticipants().stream().map(
                        (participation) -> {
                            return new EmployeeIdEmailDto(
                                    participation.getEmployee().getId(),
                                    participation.getEmployee().getEmail()
                            );
                        }
                ).toList()
        );
    }
}
