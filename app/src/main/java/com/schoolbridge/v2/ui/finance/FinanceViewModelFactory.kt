package com.schoolbridge.v2.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.schoolbridge.v2.data.repository.interfaces.FinanceRepository
import com.schoolbridge.v2.data.session.UserSessionManager

class FinanceViewModelFactory(
    private val financeRepository: FinanceRepository,
    private val userSessionManager: UserSessionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(financeRepository, userSessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
