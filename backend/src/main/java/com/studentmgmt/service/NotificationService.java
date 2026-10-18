package com.studentmgmt.service;

import com.studentmgmt.dto.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Page<NotificationDTO> getMyNotifications(Boolean isRead, Pageable pageable);

    long getUnreadCount();

    void markAsRead(Long id);

    void markAllAsRead();

    void delete(Long id);
}