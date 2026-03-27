package com.schoolbridge.v2.data.repository.implementations

import com.schoolbridge.v2.data.dto.finance.MobileFinanceDashboardDto
import com.schoolbridge.v2.data.remote.FinanceApiService
import com.schoolbridge.v2.data.repository.interfaces.FinanceRepository

class FinanceRepositoryImpl(
    private val financeApiService: FinanceApiService
) : FinanceRepository {
    override suspend fun getFinanceDashboard(userId: String): MobileFinanceDashboardDto {
        return financeApiService.getFinanceDashboard(userId)
    }
}
