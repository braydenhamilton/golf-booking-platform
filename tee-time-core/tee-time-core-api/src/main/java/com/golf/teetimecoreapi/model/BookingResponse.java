package com.golf.teetimecoreapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {
    private String bookingId;
    private String status;
    private String confirmationNumber;
    private String message;
}