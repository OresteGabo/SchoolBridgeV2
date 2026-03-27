package com.schoolbridge.v2.ui.finance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate
import kotlin.math.abs

data class FinanceDashboard(
    val students: List<FinanceStudent>,
    val selectedStudentId: String,
    val stats: List<FinanceStat>,
    val outstandingItems: List<OutstandingChargeUi>,
    val transactions: List<FinanceTransactionUi>
) {
    fun statsFor(studentId: String): List<FinanceStat> {
        if (studentId == ALL_STUDENTS_ID) return stats

        val studentName = students.firstOrNull { it.id == studentId }?.name ?: "Student"
        val studentTransactions = transactions.filter { it.studentId == studentId }
        val studentOutstanding = outstandingItems.filter { it.studentId == studentId }

        val totalPaid = studentTransactions
            .filter { it.status == FinanceStatus.Completed }
            .sumOf { abs(it.amount) }
        val pending = studentOutstanding.sumOf { it.amount }
        val dueSoon = studentOutstanding.minByOrNull { it.dueDate }?.dueDateLabel ?: "No due item"
        val chatLinked = studentTransactions.count { it.source == FinanceSource.Chat }

        return listOf(
            FinanceStat("Paid", totalPaid.asMoney(), "Completed for $studentName", Icons.Default.CheckCircle, FinanceTone.Positive),
            FinanceStat("Outstanding", pending.asMoney(), "Still waiting for settlement", Icons.Default.WarningAmber, FinanceTone.Warning),
            FinanceStat("Next due", dueSoon, "Most urgent pending charge", Icons.Default.Schedule, FinanceTone.Info),
            FinanceStat("From chat", "$chatLinked tracked", "Payment actions originating in messages", Icons.Default.ChatBubbleOutline, FinanceTone.Accent)
        )
    }

    fun outstandingItemsFor(studentId: String): List<OutstandingChargeUi> {
        return if (studentId == ALL_STUDENTS_ID) outstandingItems else outstandingItems.filter { it.studentId == studentId }
    }

    fun pendingAmountLabel(studentId: String): String {
        val amount = outstandingItemsFor(studentId).sumOf { it.amount }
        return amount.asMoney()
    }

    fun paidThisMonthLabel(studentId: String): String {
        val amount = transactions
            .filter { it.status == FinanceStatus.Completed }
            .filter { studentId == ALL_STUDENTS_ID || it.studentId == studentId }
            .sumOf { abs(it.amount) }
        return amount.asMoney()
    }

    companion object {
        fun empty(): FinanceDashboard = FinanceDashboard(
            students = listOf(FinanceStudent(ALL_STUDENTS_ID, "All students")),
            selectedStudentId = ALL_STUDENTS_ID,
            stats = listOf(
                FinanceStat("Total paid", 0.0.asMoney(), "Across completed family payments", Icons.Default.CheckCircle, FinanceTone.Positive),
                FinanceStat("Outstanding", 0.0.asMoney(), "Still waiting to be settled", Icons.Default.WarningAmber, FinanceTone.Warning),
                FinanceStat("Next due", "No due item", "Closest upcoming due date", Icons.Default.Schedule, FinanceTone.Info),
                FinanceStat("Chat linked", "0 transactions", "Started in messages and tracked here", Icons.Default.ChatBubbleOutline, FinanceTone.Accent)
            ),
            outstandingItems = emptyList(),
            transactions = emptyList()
        )
    }
}

data class FinanceStudent(
    val id: String,
    val name: String
)

data class FinanceStat(
    val label: String,
    val value: String,
    val note: String,
    val icon: ImageVector,
    val tone: FinanceTone
)

data class OutstandingChargeUi(
    val id: String,
    val studentId: String,
    val studentName: String,
    val title: String,
    val description: String,
    val amount: Double,
    val amountLabel: String,
    val dueDate: LocalDate,
    val dueDateLabel: String,
    val category: FinanceCategory,
    val fromChat: Boolean,
    val isUrgent: Boolean
)

data class FinanceTransactionUi(
    val id: String,
    val studentId: String,
    val studentName: String,
    val title: String,
    val description: String,
    val amount: Double,
    val amountLabel: String,
    val dateLabel: String,
    val paymentMethod: String,
    val reference: String,
    val status: FinanceStatus,
    val category: FinanceCategory,
    val source: FinanceSource
)

data class QuickActionUi(
    val label: String,
    val icon: ImageVector,
    val tone: FinanceTone
)

enum class FinanceTone {
    Positive,
    Warning,
    Info,
    Accent,
    Error
}

enum class FinanceSection(val label: String) {
    Overview("Overview"),
    Due("Due"),
    Activity("Activity")
}

enum class FinanceFilter(val label: String) {
    All("All"),
    Outstanding("Outstanding"),
    Chat("From chat"),
    Completed("Completed")
}

enum class FinanceStatus(val label: String) {
    Completed("Completed"),
    Pending("Pending"),
    Failed("Failed");

    companion object {
        fun fromApi(value: String): FinanceStatus = when (value.uppercase()) {
            "PENDING" -> Pending
            "FAILED" -> Failed
            else -> Completed
        }
    }
}

enum class FinanceSource(val label: String) {
    Chat("Paid in chat"),
    Finance("Paid in finance"),
    Admin("Recorded by admin");

    companion object {
        fun fromApi(value: String): FinanceSource = when (value.uppercase()) {
            "CHAT" -> Chat
            "ADMIN" -> Admin
            else -> Finance
        }
    }
}

enum class FinanceCategory(val label: String, val icon: ImageVector) {
    SchoolFees("School fees", Icons.Default.School),
    Fine("Fine", Icons.Default.WarningAmber),
    Uniform("Uniform", Icons.Default.Payments),
    Transport("Transport", Icons.Default.Schedule),
    Supplies("Supplies", Icons.AutoMirrored.Filled.ReceiptLong);

    companion object {
        fun fromApi(value: String): FinanceCategory = when (value.uppercase()) {
            "FINE" -> Fine
            "UNIFORM" -> Uniform
            "TRANSPORT" -> Transport
            "SUPPLIES" -> Supplies
            else -> SchoolFees
        }
    }
}

const val ALL_STUDENTS_ID = "all_students"

fun Double.asMoney(currency: String = "RWF"): String {
    val normalized = toLong()
    return "%,d $currency".format(normalized)
}
