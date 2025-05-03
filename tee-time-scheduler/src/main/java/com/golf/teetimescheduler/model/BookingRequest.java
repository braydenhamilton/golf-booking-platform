package com.golf.teetimescheduler.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_requests")
@Data
public class BookingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "desired_time", nullable = false)
    private LocalDateTime desiredTime;

    @Column(name = "open_time", nullable = false)
    private LocalDateTime openTime;

    @Column(nullable = false)
    private String status;

    @Column(name = "retry_count")
    private int retryCount;

}
