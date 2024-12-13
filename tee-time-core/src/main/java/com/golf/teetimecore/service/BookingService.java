package com.golf.teetimecore.service;

import com.golf.teetimecore.entity.Booking;
import com.golf.teetimecore.exception.ResourceNotFoundException;
import com.golf.teetimecore.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public Booking createBooking(Booking booking) {
        return repository.save(booking);
    }

    public void deleteBooking(Long id) {
        repository.deleteById(id);
    }

    public Booking updateBooking(Long id, Booking bookingDetails) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Booking not found");
                });
        booking.setTeeTime(bookingDetails.getTeeTime());
        booking.setMembers(bookingDetails.getMembers());
        return repository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return repository.findAll();
    }
}
