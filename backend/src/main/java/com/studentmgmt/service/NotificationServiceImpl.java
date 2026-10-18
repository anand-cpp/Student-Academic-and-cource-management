package com.studentmgmt.service;

import com.studentmgmt.dto.NotificationDTO;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.Notification;
import com.studentmgmt.repository.NotificationRepository;
import com.studentmgmt.security.UserDetailsServiceImpl;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getMyNotifications(Boolean isRead, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.studentmgmt.model.User user = userDetailsService.loadUserEntityByUsername(username);

        if (isRead != null) {
            return notificationRepository.findByUserIdAndIsRead(user.getId(), isRead, pageable)
                    .map(dtoMapper::toNotificationDTO);
        }
        return notificationRepository.findByUserId(user.getId(), pageable)
                .map(dtoMapper::toNotificationDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.studentmgmt.model.User user = userDetailsService.loadUserEntityByUsername(username);

        return notificationRepository.countByUserIdAndIsRead(user.getId(), false);
    }

    @Override
    public void markAsRead(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.studentmgmt.model.User user = userDetailsService.loadUserEntityByUsername(username);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.studentmgmt.model.User user = userDetailsService.loadUserEntityByUsername(username);

        List<Notification> unread = notificationRepository.findByUserIdAndIsRead(user.getId(), false);
        unread.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    @Override
    public void delete(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.studentmgmt.model.User user = userDetailsService.loadUserEntityByUsername(username);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        notificationRepository.delete(notification);
    }
}