package com.HRMS.HRMS.repository.Game;

import com.HRMS.HRMS.entity.GameEntities.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRequestRepository  extends JpaRepository<BookingRequest,Long> {
    List<BookingRequest> findBookingRequestBySlot_Id(Long slotId);

    List<BookingRequest> findBookingRequestByPrimaryBooker_IdAndSlot_Date(Long primaryBookerId, LocalDate slotDate);

    boolean existsByPrimaryBooker_IdAndSlot_DateAndStatus(Long primaryBookerId, LocalDate slotDate, BookingRequest.RequestStatus status);
}
