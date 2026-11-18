package com.barrowrent.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.barrowrent.request.BookingRequest;
import com.barrowrent.response.BookingResponse;
import com.barrowrent.security.CurrentUser;
import com.barrowrent.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // CREATE BOOKING

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @Valid @RequestBody BookingRequest request) {

        return bookingService.createBooking(
            request,
            CurrentUser.getId()
        );
    }

    // GET ONE BOOKING

    @GetMapping("/{id}")
    public BookingResponse getBooking(
            @PathVariable Long id) {

        return bookingService.getBooking(
            id,
            CurrentUser.getId()
        );
    }

    // BORROWER DASHBOARD

    @GetMapping("/my-bookings")
    public List<BookingResponse> getMyBookings() {

        Long userId = CurrentUser.getId();

        return bookingService.getBorrowerBookings(
            userId,
            userId
        );
    }

    // OWNER DASHBOARD

    @GetMapping("/owner-bookings")
    public List<BookingResponse> getMyOwnerBookings() {

        Long userId = CurrentUser.getId();

        return bookingService.getOwnerBookings(
            userId,
            userId
        );
    }

    // OWNER APPROVES BOOKING

    @PatchMapping("/{id}/approve")
    public BookingResponse approveBooking(
            @PathVariable Long id) {

        return bookingService.approveBooking(
            id,
            CurrentUser.getId()
        );
    }

    // OWNER REJECTS BOOKING

    @PatchMapping("/{id}/reject")
    public BookingResponse rejectBooking(
            @PathVariable Long id) {

        return bookingService.rejectBooking(
            id,
            CurrentUser.getId()
        );
    }

    // BORROWER CANCELS BOOKING

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancelBooking(
            @PathVariable Long id) {

        return bookingService.cancelBooking(
            id,
            CurrentUser.getId()
        );
    }

    // OWNER ACTIVATES RENTAL

    @PatchMapping("/{id}/activate")
    public BookingResponse activateBooking(
            @PathVariable Long id) {

        return bookingService.activateBooking(
            id,
            CurrentUser.getId()
        );
    }

    // OWNER COMPLETES RENTAL

    @PatchMapping("/{id}/complete")
    public BookingResponse completeBooking(
            @PathVariable Long id) {

        return bookingService.completeBooking(
            id,
            CurrentUser.getId()
        );
    }
}
