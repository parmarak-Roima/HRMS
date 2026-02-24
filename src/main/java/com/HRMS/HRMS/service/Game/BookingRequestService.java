package com.HRMS.HRMS.service.Game;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.dto.EmailDtos.*;
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
import com.HRMS.HRMS.service.Email.EmailContentBuilder;
import com.HRMS.HRMS.service.Email.EmailService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final EmailContentBuilder emailContentBuilder;
    private final EmailService emailService;

    public BookingRequestService(
            BookingRequestRepository bookingRequestRepository,
            BookingParticipantRepository bookingParticipantRepository,
            GameSlotsRepository gameSlotsRepository, EmployeeRepository employeeRepository, GameInterestRepository gameInterestRepository, GameInterestService gameInterestService, EmailContentBuilder emailContentBuilder, EmailService emailService){
        this.bookingParticipantRepository = bookingParticipantRepository;
        this.bookingRequestRepository = bookingRequestRepository;
        this.gameSlotsRepository = gameSlotsRepository;
        this.employeeRepository = employeeRepository;
        this.gameInterestRepository = gameInterestRepository;
        this.gameInterestService = gameInterestService;
        this.emailContentBuilder = emailContentBuilder;
        this.emailService = emailService;
    }

    @Transactional
    public BookingRequestResponseDto createBookingRequest(BookingRequestCreateDto dto, CustomUserPrincipal user){
        //validation
        GameSlot gameSlot = gameSlotsRepository.findById(dto.getSlotId()).orElseThrow(
                () ->     new ResourceNotFoundException("slot not found!!")
        );
        //validate min and max player
        if(!(gameSlot.getGame().getMinPlayers() <= dto.getParticipantsId().size() +1
                && gameSlot.getGame().getMaxPlayers() >= dto.getParticipantsId().size() + 1)){
            throw new IllegalArgumentException("give no of participants according to game ");
        }
        //employee can make booking request only before 45 minutes
        LocalDateTime slotStartDateTime = LocalDateTime.of(gameSlot.getDate(), gameSlot.getStartTime());
        if (slotStartDateTime.minusMinutes(90).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Booking request time is over! You must book at least 1.5 hours in advance.");
        }
        //check for active booking of primary booker
        if (isActiveBooking(gameSlot.getDate(),user.getId())) {
            throw new IllegalArgumentException("Fairness Rule: You already have a active booking request for this date!");
        }
        Employee primaryBooker = employeeRepository.findById(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("primary booker not found!!")
        );
        GameInterest interest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(
                gameSlot.getGame().getId(), primaryBooker.getId()
        );
        //check that primary booker is interested or not
        if (interest == null || !interest.isInterested()) {
            throw new IllegalArgumentException("You must mark 'Interest' in this game profile before booking!");
        }
        int playCount = interest.getPlayedInCurrentCycle();

        //create booking request
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setSlot(gameSlot);
        bookingRequest.setStatus(BookingRequest.RequestStatus.PENDING);
        bookingRequest.setPrimaryBooker(primaryBooker);
        log.info("User {} is attempting to book Slot ID {}", user.getEmail(), dto.getSlotId());
        if (playCount == 0 && gameSlot.getStatus().equals(GameSlot.SlotStatus.OPEN) ) {
            bookingRequest.setStatus(BookingRequest.RequestStatus.CONFIRMED);
            gameSlot.setStatus(GameSlot.SlotStatus.BOOKED);
            gameSlotsRepository.save(gameSlot);
            interest.setPlayedInCurrentCycle(playCount+1);
            gameInterestRepository.save(interest);
            checkAndResetCycle(gameSlot.getGame().getId());
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
//                    //check for friends active booking
                    if (isActiveBooking(gameSlot.getDate(),participant.getId())) {
                        throw new IllegalArgumentException("Fairness Rule: Your friends already have a active booking request for this date!");
                    }
                    GameInterest gameInterest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id( gameSlot.getGame().getId() , participant.getId());
                    if(!gameInterest.isInterested()){
                       throw new IllegalArgumentException("participants is not interested in this game");
                    }
                    saveParticipant(savedRequest,participant);
                    //if confirmed increase friends count tooo
                    if (savedRequest.getStatus() == BookingRequest.RequestStatus.CONFIRMED) {
                        GameInterest friendInterest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(
                                gameSlot.getGame().getId(), id
                        );
                        if (friendInterest != null){
                            friendInterest.setPlayedInCurrentCycle(friendInterest.getPlayedInCurrentCycle()+1);
                            gameInterestRepository.save(friendInterest);
                        }

                    }
                    //send mail to participants
                    sendBookingRequestMail(participant,bookingRequest);
                }

        );

        sendBookingRequestMail( primaryBooker , bookingRequest );

        return new BookingRequestResponseDto(
                savedRequest.getId(),
                savedRequest.getStatus().toString(),
                savedRequest.getSlot().getId(),
                savedRequest.getSlot().getStatus().toString(),
                savedRequest.getSlot().getGame().getName(),
                savedRequest.getSlot().getStartTime(),
                savedRequest.getSlot().getEndTime(),
                savedRequest.getSlot().getDate(),
                savedRequest.getRequestedAt(),
                savedRequest.getPrimaryBooker().getId(),
                savedRequest.getPrimaryBooker().getEmail(),
               null
        );
    }

    private void sendBookingRequestMail(Employee emp,BookingRequest bookingRequest){
        String body = emailContentBuilder.buildEmail("BookingRequestStatus",new BookingRequestEmailDto(
                emp.getName(), bookingRequest.getSlot().getGame().getName() , bookingRequest.getStatus().toString(), bookingRequest.getSlot().getDate(),bookingRequest.getSlot().getStartTime(),bookingRequest.getSlot().getEndTime(),bookingRequest.getPrimaryBooker().getName()
        ));
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body , emp.getEmail(),"Booking request was made ",null
                )
        );
    }

    private boolean isActiveBooking(LocalDate date,Long userId){
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!!"));
        List<BookingRequest> allUserBookings = employee.getBookingParticipants().stream()
                .map(BookingParticipant::getBookingRequest)
                .toList();

        LocalDateTime now = LocalDateTime.now();

        return allUserBookings.stream().anyMatch(booking -> {
            boolean isSameDate = booking.getSlot().getDate().equals(date);
            // Must be CONFIRMED
            boolean isActiveStatus = (booking.getStatus() == BookingRequest.RequestStatus.CONFIRMED);

            //Has NOT played yet (Slot's End Time is in the future)
            LocalDateTime slotEndDateTime = LocalDateTime.of(booking.getSlot().getDate(), booking.getSlot().getEndTime());
            boolean isNotPlayedYet = slotEndDateTime.isAfter(now);

            return isSameDate && isActiveStatus && isNotPlayedYet;
        });
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
        LocalDateTime slotStartDateTime = LocalDateTime.of(request.getSlot().getDate(), request.getSlot().getStartTime());

        if (slotStartDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("You cannot cancel a past booking!");
        }
        //Only the Booker can cancel
        if (!request.getPrimaryBooker().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only cancel your own bookings!");
        }
        if (request.getStatus() == BookingRequest.RequestStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled.");
        }
        GameSlot slot = request.getSlot();
        if (request.getStatus() == BookingRequest.RequestStatus.CONFIRMED) {
            decrementPlayCount(request.getPrimaryBooker(), slot.getGame());
            if(request.getParticipants() != null){
                for( BookingParticipant part : request.getParticipants() ){
                    if(!part.getEmployee().getId().equals(request.getPrimaryBooker().getId())){
                        decrementPlayCount(part.getEmployee(),slot.getGame());
                        log.info("Decremented play counts for participants of cancelled Booking ID {}", bookingId);
                    }
                }
            }
            Long newWinnerId = getWinningBookingRequestId(slot.getId());

            if (newWinnerId != null) {
                BookingRequest newWinner = bookingRequestRepository.findById(newWinnerId).get();
                newWinner.setStatus(BookingRequest.RequestStatus.CONFIRMED);
                incrementTeamPlayCount(newWinner);
                bookingRequestRepository.save(newWinner);
                log.info("AUTO-REASSIGN: Slot {} reassigned from cancelled booking to New Winner {}", slot.getId(), newWinner.getPrimaryBooker().getEmail());
                //send mail to reassigned winner
                sendBookingRequestMail(newWinner.getPrimaryBooker(), newWinner);
                newWinner.getParticipants().forEach(part -> sendBookingRequestMail(part.getEmployee(), newWinner));
                //check for cycle
                checkAndResetCycle(slot.getGame().getId());
            } else {
                slot.setStatus(GameSlot.SlotStatus.OPEN);
                gameSlotsRepository.save(slot);
                log.info("Cancelled booking was CONFIRMED. No waitlist found. Re-opening Slot ID {}", slot.getId());
            }
        }
        request.setStatus(BookingRequest.RequestStatus.CANCELLED);
        bookingRequestRepository.save(request);
        //send cancellation mail
        sendMailForCancellation(request);
    }

    private void sendMailForCancellation(BookingRequest request){
        //send to primary booker
        sendMailToEmployeeForCancellation(request.getPrimaryBooker(),request);
        request.getParticipants().forEach(
                participant -> {
                    sendMailToEmployeeForCancellation(participant.getEmployee(),request);
                }
        );
    }

    private void sendMailToEmployeeForCancellation(Employee emp , BookingRequest bookingRequest){
        String body = emailContentBuilder.buildEmail("BookingRequestStatus",new BookingCancelledEmailDto(
                emp.getName(), bookingRequest.getSlot().getGame().getName() ,bookingRequest.getSlot().getDate(),bookingRequest.getSlot().getStartTime(),bookingRequest.getSlot().getEndTime(),bookingRequest.getPrimaryBooker().getName()
        ));
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body , emp.getEmail(),"Booking request cancelled by primary booker",null
                )
        );
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

//    @Scheduled(cron = "0 * * * * *")
    public void assignSlotToMostPrior( ){
        log.info("CRON: Starting automated slot assignment process...");
        //find slot that are after 30 minutes
        List<GameSlot> gameSlotsForToday = gameSlotsRepository.findGameSlotByDateAndStatus(LocalDate.now().plusDays(1),GameSlot.SlotStatus.OPEN);
        if( gameSlotsForToday.isEmpty()){
            log.info("CRON: No eligible OPEN slots found for assignment at this time.");
            return;
        }
           List<Long> slotIds = gameSlotsForToday.stream()
                .filter(gameSlot -> {
                    LocalTime now = LocalTime.now();
                    LocalTime oneHourFromNow = LocalTime.now().plusMinutes(61);
                   return gameSlot.getStartTime().isAfter(now) && gameSlot.getStartTime().isBefore(oneHourFromNow);
                })
                .map(GameSlot::getId
                )
                .toList();
           slotIds.forEach((slotId) -> {
                        GameSlot gameSlot = gameSlotsRepository.findById(slotId).orElseThrow(
                                () -> new ResourceNotFoundException("slot not found.")
                        );
                       Long winnerId = getWinningBookingRequestId(slotId);
                       if(winnerId!=null){
                           BookingRequest winner = bookingRequestRepository.findById(winnerId).orElseThrow(
                                   () -> new ResourceNotFoundException("request not found.")
                           );
                           log.info("CRON: Slot ID {} assigned to Winner Booking ID {} (Primary Booker: {})", slotId, winnerId, winner.getPrimaryBooker().getEmail());
                           gameSlot.setStatus(GameSlot.SlotStatus.BOOKED);
                           winner.setStatus(BookingRequest.RequestStatus.CONFIRMED);
                           incrementTeamPlayCount(winner);
                           bookingRequestRepository.save(winner);
                           gameSlotsRepository.save(gameSlot);
                            sendBookingRequestMail(
                                    winner.getPrimaryBooker(),winner
                            );
                            winner.getParticipants().forEach(
                                    participant -> {
                                        sendBookingRequestMail(
                                                participant.getEmployee() , winner
                                        );
                                    }
                            );
                           checkAndResetCycle(gameSlot.getGame().getId());
                       }
                   }
           );

    }

    private void sendMailForRejection(BookingRequest bookingRequest){
        //send to primary booker
        sendMailToEmployeeForRejection(bookingRequest.getPrimaryBooker(),bookingRequest);
        //send to all other participants
       bookingRequest.getParticipants().forEach(
               participant -> {
                   sendMailToEmployeeForRejection(participant.getEmployee(),bookingRequest);
               }
       );
    }

    private void sendMailToEmployeeForRejection(Employee emp,BookingRequest bookingRequest){
        String body = emailContentBuilder.buildEmail("BookingRequestStatus",new BookingRejectedEmailDto(
                emp.getName(), bookingRequest.getSlot().getGame().getName() ,bookingRequest.getSlot().getDate(),bookingRequest.getSlot().getStartTime(),bookingRequest.getSlot().getEndTime()
        ));
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body , emp.getEmail(),"Booking request rejected by system",null
                )
        );
    }

    public Long getWinningBookingRequestId(Long slotId) {

        GameSlot slot = gameSlotsRepository.findById(slotId).orElseThrow(
                () -> new ResourceNotFoundException("slot not exits!!")
        );

        List<BookingRequest>  pendingRequests = slot.getBookingRequests().stream().filter(bookingRequest ->
                    bookingRequest.getStatus() == BookingRequest.RequestStatus.PENDING

        ).toList();
        if (pendingRequests.isEmpty()) {
            log.info("CRON: No pending requests found for Slot ID {}", slotId);
            return null;
        }

        //go with average of team
        BookingRequest winner = pendingRequests.stream()
                .min(Comparator
                        .comparingDouble(this::getAveragePlayCount)
                        .thenComparing(BookingRequest::getRequestedAt)
                )
                .orElse(null);

        return winner.getId();
    }

    private double getAveragePlayCount(BookingRequest req) {
        Long gameId = req.getSlot().getGame().getId();

        // Start with the Primary Booker
        int totalPlays = getPlayCount(req.getPrimaryBooker().getId(), gameId);
        int totalPeople = 1;

        // Add the Participants
        if (req.getParticipants() != null) {
            for (BookingParticipant participant : req.getParticipants()) {
                if (!participant.getEmployee().getId().equals(req.getPrimaryBooker().getId())) {
                    totalPlays += getPlayCount(participant.getEmployee().getId(), gameId);
                    totalPeople++;
                }
            }
        }
        return (double) totalPlays /totalPeople;
    }

    private void checkAndResetCycle(Long gameId) {
        long peopleWaitingToPlay = gameInterestRepository.countGameInterestByGame_IdAndIsInterestedAndPlayedInCurrentCycle(gameId, true,0);

        if (peopleWaitingToPlay == 0) {
            log.info("Cycle Complete for Game ID: {}! Resetting counts to 0...", gameId);
            gameInterestRepository.resetPlayedCountForGame(gameId);
        }
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

    private void incrementTeamPlayCount(BookingRequest req) {
        Long gameId = req.getSlot().getGame().getId();
        incrementPlayCount(req.getPrimaryBooker(), gameId);
        if (req.getParticipants() != null) {
            for (BookingParticipant part : req.getParticipants()) {
                if (!part.getEmployee().getId().equals(req.getPrimaryBooker().getId())) {
                    incrementPlayCount(part.getEmployee(), gameId);
                }
            }
        }
    }

    private void incrementPlayCount(Employee emp, Long gameId) {
        GameInterest interest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(gameId, emp.getId());
        if (interest != null) {
            interest.setPlayedInCurrentCycle(interest.getPlayedInCurrentCycle() + 1);
            gameInterestRepository.save(interest);
        }
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
                req.getSlot().getDate(),
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
