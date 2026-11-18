
package com.barrowrent.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.barrowrent.response.NotificationResponse;
import com.barrowrent.security.CurrentUser;
import com.barrowrent.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // GET ALL NOTIFICATIONS

    @GetMapping
    public ResponseEntity<List<NotificationResponse>>
            getMyNotifications() {

        return ResponseEntity.ok(
            notificationService.getMyNotifications(
                CurrentUser.getId()
            )
        );
    }

    // GET UNREAD NOTIFICATIONS

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>>
            getUnreadNotifications() {

        return ResponseEntity.ok(
            notificationService.getUnreadNotifications(
                CurrentUser.getId()
            )
        );
    }

    // GET UNREAD COUNT

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {

        return ResponseEntity.ok(
            notificationService.getUnreadCount(
                CurrentUser.getId()
            )
        );
    }

    // MARK NOTIFICATION AS READ

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(
            notificationId,
            CurrentUser.getId()
        );

        return ResponseEntity.noContent().build();
    }
}
