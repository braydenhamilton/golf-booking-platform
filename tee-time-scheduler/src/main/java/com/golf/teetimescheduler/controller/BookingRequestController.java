package com.golf.teetimescheduler.controller;

import com.golf.teetimescheduler.model.BookingRequest;
import com.golf.teetimescheduler.repository.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class BookingRequestController {

    private final BookingRequestRepository repository;

    @PostMapping
    public ResponseEntity<?> scheduleBooking(@RequestBody BookingRequest request) {
        request.setStatus("PENDING");
        request.setRetryCount(0);
        repository.save(request);
        return ResponseEntity.ok("Booking scheduled");
    }
}
