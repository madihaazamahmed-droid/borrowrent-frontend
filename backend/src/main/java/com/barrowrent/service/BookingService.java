package com.barrowrent.service;

import java.util.List;

import com.barrowrent.request.BookingRequest;
import com.barrowrent.response.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(
        BookingRequest request,
        Long currentUserId
    );

    BookingResponse getBooking(
        Long bookingId,
        Long currentUserId
    );

    List<BookingResponse> getBorrowerBookings(
        Long borrowerId,
        Long currentUserId
    );

    List<BookingResponse> getOwnerBookings(
        Long ownerId,
        Long currentUserId
    );

    BookingResponse approveBooking(
        Long bookingId,
        Long currentUserId
    );

    BookingResponse rejectBooking(
        Long bookingId,
        Long currentUserId
    );

    BookingResponse cancelBooking(
        Long bookingId,
        Long currentUserId
    );

    BookingResponse activateBooking(
        Long bookingId,
        Long currentUserId
    );

    BookingResponse completeBooking(
        Long bookingId,
        Long currentUserId
    );
}