package com.barrowrent.request;

import com.barrowrent.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;

public class BookingStatusUpdate {

    @NotNull(message = "Booking status is required")
    private BookingStatus status;

    private String message;

    public BookingStatusUpdate() {
    }

    public BookingStatusUpdate(BookingStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
