package com.barrowrent.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barrowrent.entity.Booking;
import com.barrowrent.enums.BookingStatus;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    // Borrower's bookings
    List<Booking> findByBorrowerIdOrderByRequestedAtDesc(
        Long borrowerId
    );

    // Bookings for all items belonging to an owner
    List<Booking> findByItemOwnerIdOrderByRequestedAtDesc(
        Long ownerId
    );

    List<Booking> findByStatus(
        BookingStatus status
    );

    // Checks whether another booking overlaps the requested dates.
    boolean existsByItemIdAndStatusInAndStartDateLessThanAndEndDateGreaterThan(
        Long itemId,
        Collection<BookingStatus> statuses,
        LocalDate requestedEndDate,
        LocalDate requestedStartDate
    );

    // Used for notification/dashboard counts if needed.
    long countByBorrowerIdAndStatus(
        Long borrowerId,
        BookingStatus status
    );

    long countByItemOwnerIdAndStatus(
        Long ownerId,
        BookingStatus status
    );
}
