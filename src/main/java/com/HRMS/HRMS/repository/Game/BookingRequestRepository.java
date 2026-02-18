package com.HRMS.HRMS.repository.Game;

import com.HRMS.HRMS.entity.GameEntities.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRequestRepository  extends JpaRepository<BookingRequest,Long> {
    List<BookingRequest> findBookingRequestBySlot_Id(Long slotId);
}
