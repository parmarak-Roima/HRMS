package com.HRMS.HRMS.dto.GameDtos;


import com.HRMS.HRMS.entity.GameEntities.GameSlot;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequestCreateDto {
    @NotNull
    private Long slotId;
    @NotNull
    private Long primaryBookerId;
    @NotNull
    private List<Long> participantsId;
}
