package com.golf.teetimecore.dto;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ModifyBookingRequest {

    @Getter
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @Future(message = "Tee time must be in the future")
    private LocalDate newDate;

    private LocalTime newTime;

    private List<String> newMembers;

}
