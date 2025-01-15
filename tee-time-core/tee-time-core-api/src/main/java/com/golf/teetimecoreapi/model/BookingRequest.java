package com.golf.teetimecoreapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingRequest {
    private String date;
    private String time;
    private String course;
    private Integer players;
}
