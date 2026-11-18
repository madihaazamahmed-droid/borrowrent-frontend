
package com.barrowrent.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barrowrent.entity.Booking;
import com.barrowrent.entity.Notification;
import com.barrowrent.entity.User;
import com.barrowrent.exception.BookingException;
import com.barrowrent.repository.NotificationRepository;
import com.barrowrent.response.NotificationResponse;
import com.barrowrent.service.NotificationService;

@Service
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // CREATE NOTIFICATION

    @Override
    public void createNotification(
            User recipient,
            Booking booking,
            String title,
            String message) {

        if (recipient == null || booking == null) {
            throw new IllegalArgumentException(
                "Notification recipient and booking are required."
            );
        }

        Notification notification = new Notification();

        notification.setUser(recipient);
        notification.setBooking(booking);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    // GET ALL NOTIFICATIONS OF LOGGED-IN USER

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(
            Long userId) {

        return notificationRepository
            .findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    // GET UNREAD NOTIFICATIONS

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(
            Long userId) {

        return notificationRepository
            .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    // GET UNREAD COUNT

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {

        return notificationRepository
            .countByUserIdAndReadFalse(userId);
    }

    // MARK NOTIFICATION AS READ

    @Override
    public void markAsRead(
            Long notificationId,
            Long userId) {

        Notification notification = notificationRepository
            .findByIdAndUserId(notificationId, userId)
            .orElseThrow(() -> new BookingException(
                HttpStatus.NOT_FOUND,
                "Notification not found."
            ));

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    // ENTITY TO RESPONSE MAPPER

    private NotificationResponse toResponse(
            Notification notification) {

        return new NotificationResponse(
            notification.getId(),
            notification.getBooking().getId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
