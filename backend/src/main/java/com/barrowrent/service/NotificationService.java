
package com.barrowrent.service;

import java.util.List;

import com.barrowrent.entity.Booking;
import com.barrowrent.entity.User;
import com.barrowrent.response.NotificationResponse;

public interface NotificationService {

    void createNotification(
        User recipient,
        Booking booking,
        String title,
        String message
    );

    List<NotificationResponse> getMyNotifications(
        Long userId
    );

    List<NotificationResponse> getUnreadNotifications(
        Long userId
    );

    long getUnreadCount(Long userId);

    void markAsRead(
        Long notificationId,
        Long userId
    );
}
