
package com.barrowrent.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barrowrent.entity.Booking;
import com.barrowrent.entity.Item;
import com.barrowrent.entity.User;
import com.barrowrent.enums.BookingStatus;
import com.barrowrent.enums.ItemStatus;
import com.barrowrent.exception.BookingException;
import com.barrowrent.repository.BookingRepository;
import com.barrowrent.repository.ItemRepository;
import com.barrowrent.repository.UserRepository;
import com.barrowrent.request.BookingRequest;
import com.barrowrent.response.BookingResponse;
import com.barrowrent.service.BookingService;
import com.barrowrent.service.NotificationService;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final Set<BookingStatus> BLOCKING_STATUSES =
            Set.of(
                    BookingStatus.PENDING,
                    BookingStatus.APPROVED,
                    BookingStatus.ACTIVE
            );

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            ItemRepository itemRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // CREATE BOOKING
    @Override
    public BookingResponse createBooking(
            BookingRequest request,
            Long currentUserId) {

        if (request == null
                || request.getItemId() == null
                || request.getStartDate() == null
                || request.getEndDate() == null
                || currentUserId == null) {

            throw new BookingException(
                    HttpStatus.BAD_REQUEST,
                    "Item, borrower, start date and end date are required."
            );
        }

        if (request.getMessage() != null
                && request.getMessage().length() > 500) {

            throw new BookingException(
                    HttpStatus.BAD_REQUEST,
                    "Booking message cannot exceed 500 characters."
            );
        }

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (startDate.isBefore(LocalDate.now())) {
            throw new BookingException(
                    HttpStatus.BAD_REQUEST,
                    "Booking start date cannot be in the past."
            );
        }

        if (!endDate.isAfter(startDate)) {
            throw new BookingException(
                    HttpStatus.BAD_REQUEST,
                    "End date must be after start date."
            );
        }

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new BookingException(
                        HttpStatus.NOT_FOUND,
                        "Item not found."
                ));

        // Borrower comes from the authenticated user.
        User borrower = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BookingException(
                        HttpStatus.NOT_FOUND,
                        "Authenticated user not found."
                ));

        if (item.getOwner() == null) {
            throw new BookingException(
                    HttpStatus.CONFLICT,
                    "The item has no owner."
            );
        }

        if (item.getOwner().getId().equals(currentUserId)) {
            throw new BookingException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot book your own item."
            );
        }

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new BookingException(
                    HttpStatus.CONFLICT,
                    "This item is currently unavailable."
            );
        }

        boolean overlap = bookingRepository
                .existsByItemIdAndStatusInAndStartDateLessThanAndEndDateGreaterThan(
                        item.getId(),
                        BLOCKING_STATUSES,
                        endDate,
                        startDate
                );

        if (overlap) {
            throw new BookingException(
                    HttpStatus.CONFLICT,
                    "This item is already booked for the selected dates."
            );
        }

        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);

        BigDecimal totalAmount = item.getDailyRent()
                .multiply(BigDecimal.valueOf(rentalDays));

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBorrower(borrower);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING);
        booking.setMessage(request.getMessage());

        Booking savedBooking = bookingRepository.save(booking);

        // Notify item owner.
        notificationService.createNotification(
                item.getOwner(),
                savedBooking,
                "New booking request",
                borrower.getFullName()
                        + " has requested to rent your item: "
                        + item.getName()
        );

        return toResponse(savedBooking);
    }

    // GET SINGLE BOOKING
    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(
            Long bookingId,
            Long currentUserId) {

        Booking booking = findBooking(bookingId);
        verifyBookingAccess(booking, currentUserId);

        return toResponse(booking);
    }

    // GET BORROWER BOOKINGS
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBorrowerBookings(
            Long borrowerId,
            Long currentUserId) {

        if (currentUserId == null || !currentUserId.equals(borrowerId)) {
            throw new AccessDeniedException(
                    "You can only view your own bookings."
            );
        }

        return bookingRepository
                .findByBorrowerIdOrderByRequestedAtDesc(currentUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET OWNER BOOKINGS
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getOwnerBookings(
            Long ownerId,
            Long currentUserId) {

        if (currentUserId == null || !currentUserId.equals(ownerId)) {
            throw new AccessDeniedException(
                    "You can only view bookings for your own items."
            );
        }

        return bookingRepository
                .findByItemOwnerIdOrderByRequestedAtDesc(currentUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // APPROVE BOOKING
    @Override
    public BookingResponse approveBooking(
            Long bookingId,
            Long currentUserId) {

        Booking booking = findBooking(bookingId);

        verifyBookingOwner(booking, currentUserId);
        requireStatus(booking, BookingStatus.PENDING);

        // Check other bookings for overlapping dates.
        List<Booking> ownerBookings = bookingRepository
                .findByItemOwnerIdOrderByRequestedAtDesc(currentUserId);

        boolean hasOtherConflict = ownerBookings.stream()
                .filter(other -> !other.getId().equals(booking.getId()))
                .anyMatch(other ->
                        BLOCKING_STATUSES.contains(other.getStatus())
                                && other.getItem().getId()
                                        .equals(booking.getItem().getId())
                                && other.getStartDate()
                                        .isBefore(booking.getEndDate())
                                && other.getEndDate()
                                        .isAfter(booking.getStartDate())
                );

        if (hasOtherConflict) {
            throw new BookingException(
                    HttpStatus.CONFLICT,
                    "Another booking overlaps with these dates."
            );
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setApprovedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                savedBooking.getBorrower(),
                savedBooking,
                "Booking approved",
                "Your booking for "
                        + savedBooking.getItem().getName()
                        + " has been approved."
        );

        return toResponse(savedBooking);
    }

    // REJECT BOOKING
    @Override
    public BookingResponse rejectBooking(
            Long bookingId,
            Long currentUserId) {

        Booking booking = findBooking(bookingId);

        verifyBookingOwner(booking, currentUserId);
        requireStatus(booking, BookingStatus.PENDING);

        booking.setStatus(BookingStatus.REJECTED);

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                savedBooking.getBorrower(),
                savedBooking,
                "Booking rejected",
                "Your booking for "
                        + savedBooking.getItem().getName()
                        + " has been rejected."
        );

        return toResponse(savedBooking);
    }

    // CANCEL BOOKING
    @Override
    public BookingResponse cancelBooking(
            Long bookingId,
            Long currentUserId) {

        Booking booking = findBooking(bookingId);

        verifyBookingBorrower(booking, currentUserId);

        if (booking.getStatus() != BookingStatus.PENDING
                && booking.getStatus() != BookingStatus.APPROVED) {

            throw new BookingException(
                    HttpStatus.CONFLICT,
                    "Only pending or approved bookings can be cancelled."
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                savedBooking.getItem().getOwner(),
                savedBooking,
                "Booking cancelled",
                "The borrower has cancelled the booking for "
                        + savedBooking.getItem().getName()
        );

        return toResponse(savedBooking);
    }

    // ACTIVATE RENTAL
    @Override
    public BookingResponse activateBooking(
            Long bookingId,
            Long currentUserId) {

        Booking booking = findBooking(bookingId);

        verifyBookingOwner(booking, currentUserId);
        requireStatus(booking, BookingStatus.APPROVED);

        LocalDate today = LocalDate.now();

        if (today.isBefore(booking.getStartDate())
                || !today.isBefore(booking.getEndDate())) {

            throw new BookingException(
                    HttpStatus.CONFLICT,
                    "Booking can only be activated during the rental period."
            );
        }

        booking.setStatus(BookingStatus.ACTIVE);

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                savedBooking.getBorrower(),
                savedBooking,
                "Rental activated",
                "Your rental for "
                        + savedBooking.getItem().getName()
                        + " is now active."
        );

        return toResponse(savedBooking);
    }

    // COMPLETE RENTAL
    @Override
    public BookingResponse completeBooking(
            Long bookingId,
            Long currentUserId) {

        Booking booking = findBooking(bookingId);

        verifyBookingOwner(booking, currentUserId);

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new BookingException(
                    HttpStatus.CONFLICT,
                    "Only active bookings can be completed."
            );
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setReturnedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                savedBooking.getBorrower(),
                savedBooking,
                "Rental completed",
                "Your rental for "
                        + savedBooking.getItem().getName()
                        + " has been marked as completed."
        );

        return toResponse(savedBooking);
    }

    // FIND BOOKING
    private Booking findBooking(Long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found with ID: " + bookingId
                ));
    }

    // VERIFY BORROWER OR OWNER ACCESS
    private void verifyBookingAccess(
            Booking booking,
            Long currentUserId) {

        if (currentUserId == null) {
            throw new AccessDeniedException(
                    "Authentication is required."
            );
        }

        Long borrowerId = booking.getBorrower().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!currentUserId.equals(borrowerId)
                && !currentUserId.equals(ownerId)) {

            throw new AccessDeniedException(
                    "You are not allowed to access this booking."
            );
        }
    }

    // VERIFY ITEM OWNER
    private void verifyBookingOwner(
            Booking booking,
            Long currentUserId) {

        if (currentUserId == null
                || booking.getItem().getOwner() == null
                || !currentUserId.equals(
                        booking.getItem().getOwner().getId())) {

            throw new AccessDeniedException(
                    "Only the item owner can perform this action."
            );
        }
    }

    // VERIFY BORROWER
    private void verifyBookingBorrower(
            Booking booking,
            Long currentUserId) {

        if (currentUserId == null
                || !currentUserId.equals(
                        booking.getBorrower().getId())) {

            throw new AccessDeniedException(
                    "Only the borrower can cancel this booking."
            );
        }
    }

    // VERIFY BOOKING STATUS
    private void requireStatus(
            Booking booking,
            BookingStatus requiredStatus) {

        if (booking.getStatus() != requiredStatus) {
            throw new BookingException(
                    HttpStatus.CONFLICT,
                    "This action requires booking status "
                            + requiredStatus
                            + ". Current status: "
                            + booking.getStatus()
            );
        }
    }

    // CONVERT ENTITY TO RESPONSE
    private BookingResponse toResponse(Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getItem().getId(),
                booking.getItem().getName(),
                booking.getBorrower().getId(),
                booking.getBorrower().getFullName(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getMessage(),
                booking.getRequestedAt(),
                booking.getApprovedAt(),
                booking.getReturnedAt()
        );
    }
}
