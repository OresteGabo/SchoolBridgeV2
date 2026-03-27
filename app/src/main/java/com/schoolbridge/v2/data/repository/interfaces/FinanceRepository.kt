package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.finance.MobileFinanceDashboardDto

interface FinanceRepository {
    suspend fun getFinanceDashboard(userId: String): MobileFinanceDashboardDto
}
