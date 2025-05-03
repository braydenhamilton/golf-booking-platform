package com.golf.teetimescheduler.repository;

import com.golf.teetimescheduler.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
    List<BookingRequest> findByOpenTimeBeforeAndStatus(LocalDateTime time, String status);
}
