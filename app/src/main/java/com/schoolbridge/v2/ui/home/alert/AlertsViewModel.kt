package com.schoolbridge.v2.ui.home.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.repository.interfaces.AlertRepository
import com.schoolbridge.v2.domain.messaging.Alert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AlertsUiState(
    val isLoading: Boolean = false,
    val alerts: List<Alert> = emptyList(),
    val errorMessage: String? = null
)

class AlertsViewModel(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState(isLoading = true))
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                alertRepository.getAlerts()
            }.onSuccess { alerts ->
                _uiState.value = AlertsUiState(
                    isLoading = false,
                    alerts = alerts,
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Could not load updates"
                )
            }
        }
    }

    fun markAsRead(id: String) {
        val alert = _uiState.value.alerts.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                alerts = _uiState.value.alerts.map { existing ->
                    if (existing.id == id) existing.copy(isRead = true, unreadMessageIds = emptyList()) else existing
                }
            )
            alertRepository.markAsRead(alert)
        }
    }

    fun markAllAsRead() {
        val unreadAlerts = _uiState.value.alerts.filterNot(Alert::isRead)
        if (unreadAlerts.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                alerts = _uiState.value.alerts.map { it.copy(isRead = true, unreadMessageIds = emptyList()) }
            )
            alertRepository.markAllAsRead(unreadAlerts)
        }
    }
}

class AlertsViewModelFactory(
    private val alertRepository: AlertRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertsViewModel(alertRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
