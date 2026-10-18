package com.studentmgmt.service;

import com.studentmgmt.dto.DashboardStatsDTO;

public interface DashboardService {

    DashboardStatsDTO getDashboardStats();

    DashboardStatsDTO getFacultyDashboardStats();

    DashboardStatsDTO getStudentDashboardStats();
}