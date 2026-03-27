package com.schoolbridge.v2.data.dto.finance

import kotlinx.serialization.Serializable

@Serializable
data class MobileFinanceDashboardDto(
    val students: List<MobileStudentDto>,
    val selectedStudentId: String,
    val stats: List<MobileFinanceStatDto>,
    val outstandingItems: List<MobileOutstandingChargeDto>,
    val transactions: List<MobileFinanceTransactionDto>
)

@Serializable
data class MobileStudentDto(
    val id: String,
    val name: String,
    val firstName: String? = null,
    val lastName: String? = null
)

@Serializable
data class MobileFinanceStatDto(
    val label: String,
    val value: String,
    val note: String,
    val tint: String
)

@Serializable
data class MobileOutstandingChargeDto(
    val id: String,
    val studentId: String,
    val studentName: String,
    val title: String,
    val description: String,
    val amount: Double,
    val amountLabel: String,
    val dueDate: String,
    val dueDateLabel: String,
    val category: String,
    val fromChat: Boolean,
    val isUrgent: Boolean
)

@Serializable
data class MobileFinanceTransactionDto(
    val id: String,
    val studentId: String,
    val studentName: String,
    val title: String,
    val description: String,
    val amount: Double,
    val amountLabel: String,
    val date: String,
    val dateLabel: String,
    val paymentMethod: String,
    val reference: String,
    val status: String,
    val category: String,
    val source: String
)
