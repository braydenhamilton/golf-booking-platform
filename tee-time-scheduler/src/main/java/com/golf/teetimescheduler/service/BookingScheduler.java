package com.golf.teetimescheduler.service;

import com.golf.teetimescheduler.model.BookingRequest;
import com.golf.teetimescheduler.repository.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingRequestRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRateString = "${scheduler.poll-interval:60}000")
    public void checkAndProcessBookings() {
        List<BookingRequest> dueRequests = repository.findByOpenTimeBeforeAndStatus(LocalDateTime.now(), "PENDING");

        for (BookingRequest req : dueRequests) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(
                        "http://tee-time-core-api.internal/api/makeBooking",
                        Map.of("userId", req.getUserId(), "courseId", req.getCourseId(), "desiredTime", req.getDesiredTime()),
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    req.setStatus("SUCCESS");
                } else {
                    req.setStatus("FAILED");
                }

            } catch (Exception ex) {
                logger.error("Error processing booking request with ID {}: {}", req.getId(), ex.getMessage(), ex);
                req.setRetryCount(req.getRetryCount() + 1);
                req.setStatus("RETRYING");
                // Optionally add backoff logic
            }

            repository.save(req);
        }
    }
}
