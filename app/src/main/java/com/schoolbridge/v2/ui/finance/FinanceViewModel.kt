package com.schoolbridge.v2.ui.finance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WarningAmber
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.finance.MobileFinanceDashboardDto
import com.schoolbridge.v2.data.repository.interfaces.FinanceRepository
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.user.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FinanceUiState(
    val isLoading: Boolean = false,
    val dashboard: FinanceDashboard = FinanceDashboard.empty(),
    val errorMessage: String? = null
)

class FinanceViewModel(
    private val financeRepository: FinanceRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState(isLoading = true))
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    fun loadFinance(userId: String) {
        if (_uiState.value.isLoading && _uiState.value.dashboard.transactions.isEmpty() && _uiState.value.dashboard.outstandingItems.isEmpty()) {
            // first load can continue
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            runCatching {
                financeRepository.getFinanceDashboard(userId)
            }.onSuccess { response ->
                syncLinkedStudents(response)
                _uiState.value = FinanceUiState(
                    isLoading = false,
                    dashboard = response.toFinanceDashboard(),
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Could not load finance data"
                )
            }
        }
    }

    private suspend fun syncLinkedStudents(response: MobileFinanceDashboardDto) {
        val currentUser = userSessionManager.currentUser.value
        if (currentUser?.isParent() != true) {
            userSessionManager.updateLinkedStudents(emptyList())
            return
        }

        val linkedStudents = response.students
            .filterNot { it.id == ALL_STUDENTS_ID }
            .map { student ->
                val fallbackFirstName = student.name.substringBefore(' ', student.name).trim()
                val fallbackLastName = student.name.substringAfter(' ', "").trim()
                CurrentUser.LinkedStudent(
                    id = student.id,
                    firstName = student.firstName?.takeIf { it.isNotBlank() } ?: fallbackFirstName,
                    lastName = student.lastName?.takeIf { it.isNotBlank() } ?: fallbackLastName
                )
            }

        userSessionManager.updateLinkedStudents(linkedStudents)
    }
}

private fun MobileFinanceDashboardDto.toFinanceDashboard(): FinanceDashboard {
    return FinanceDashboard(
        students = students.map { FinanceStudent(id = it.id, name = it.name) },
        selectedStudentId = selectedStudentId,
        stats = stats.map { stat ->
            FinanceStat(
                label = stat.label,
                value = stat.value,
                note = stat.note,
                icon = statIcon(stat.label),
                tone = statTone(stat.label)
            )
        },
        outstandingItems = outstandingItems.map { item ->
            OutstandingChargeUi(
                id = item.id,
                studentId = item.studentId,
                studentName = item.studentName,
                title = item.title,
                description = item.description,
                amount = item.amount,
                amountLabel = item.amountLabel,
                dueDate = runCatching { LocalDate.parse(item.dueDate) }.getOrDefault(LocalDate.now()),
                dueDateLabel = item.dueDateLabel,
                category = FinanceCategory.fromApi(item.category),
                fromChat = item.fromChat,
                isUrgent = item.isUrgent
            )
        },
        transactions = transactions.map { transaction ->
            FinanceTransactionUi(
                id = transaction.id,
                studentId = transaction.studentId,
                studentName = transaction.studentName,
                title = transaction.title,
                description = transaction.description,
                amount = transaction.amount,
                amountLabel = transaction.amountLabel,
                dateLabel = transaction.dateLabel,
                paymentMethod = transaction.paymentMethod,
                reference = transaction.reference,
                status = FinanceStatus.fromApi(transaction.status),
                category = FinanceCategory.fromApi(transaction.category),
                source = FinanceSource.fromApi(transaction.source)
            )
        }
    )
}

private fun statIcon(label: String) = when {
    label.contains("paid", ignoreCase = true) -> Icons.Default.CheckCircle
    label.contains("outstanding", ignoreCase = true) -> Icons.Default.WarningAmber
    label.contains("due", ignoreCase = true) -> Icons.Default.Schedule
    else -> Icons.Default.ChatBubbleOutline
}

private fun statTone(label: String) = when {
    label.contains("paid", ignoreCase = true) -> FinanceTone.Positive
    label.contains("outstanding", ignoreCase = true) -> FinanceTone.Warning
    label.contains("due", ignoreCase = true) -> FinanceTone.Info
    else -> FinanceTone.Accent
}
