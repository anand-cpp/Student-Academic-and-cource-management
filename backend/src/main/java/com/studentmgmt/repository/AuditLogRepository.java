package com.studentmgmt.repository;

import com.studentmgmt.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageable);

    List<AuditLog> findByPerformedById(Long performedById);

    Page<AuditLog> findByPerformedById(Long performedById, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.performedAt BETWEEN :startDate AND :endDate ORDER BY a.performedAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.performedAt BETWEEN :startDate AND :endDate ORDER BY a.performedAt DESC")
    List<AuditLog> findByEntityTypeAndDateRange(@Param("entityType") String entityType, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    long countByEntityTypeAndEntityId(String entityType, Long entityId);
}