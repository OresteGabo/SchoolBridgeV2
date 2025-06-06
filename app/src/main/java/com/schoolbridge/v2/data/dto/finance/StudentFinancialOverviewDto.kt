package com.schoolbridge.v2.data.dto.finance

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Student Financial Overview**.
 *
 * This DTO provides a summary of a student's financial status for a given academic context,
 * as it is exchanged with the backend API. It aggregates key financial figures such as
 * total fees, amounts paid, and outstanding balance.
 *
 * **When to use this class:**
 * You'll use `StudentFinancialOverviewDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a student's current financial standing (e.g., for a parent to see their child's tuition balance).
 * 2.  **Displaying dashboards:** Used to populate financial widgets or overview screens.
 *
 * **How to use it:**
 * `StudentFinancialOverviewDto` objects are typically received from your API layer and then **mapped** to your domain
 * `StudentFinancialOverview` model for comprehensive use in your application's UI and reporting logic.
 *
 * **Real-life Example:**
 * -   A parent logs in and sees a card showing "Balance Due: $500.00" for their child.
 * -   An administrator retrieves a summary of all outstanding balances for a specific academic year.
 *
 * @property studentId The ID of the student to whom this financial overview belongs. Example: "STUD001"
 * @property academicYearId The ID of the academic year this overview pertains to. Nullable, if it's a cumulative overview. Example: "AY2024-2025"
 * @property totalFees The total amount of fees charged to the student for the relevant period. Example: 1500.00
 * @property totalPaid The total amount of money the student (or their guardian) has paid. Example: 1000.00
 * @property balance The remaining balance due (totalFees - totalPaid). Example: 500.00
 * @property currency The currency for all financial figures, as an ISO 4217 code. Example: "USD", "RWF"
 * @property lastPaymentDate The date of the student's most recent payment, as an ISO 8601 date string. Nullable if no payments yet. Example: "2025-05-15"
 * @property dueDateForNextInstallment The due date for the next payment installment, if a payment schedule exists. Nullable. Example: "2025-07-01"
 * @property nextInstallmentAmount The amount of the next upcoming payment installment. Nullable. Example: 250.00
 */
data class StudentFinancialOverviewDto(
    @SerializedName("student_id") val studentId: String,
    @SerializedName("academic_year_id") val academicYearId: String?,
    @SerializedName("total_fees") val totalFees: Double,
    @SerializedName("total_paid") val totalPaid: Double,
    @SerializedName("balance") val balance: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("last_payment_date") val lastPaymentDate: String?,
    @SerializedName("due_date_for_next_installment") val dueDateForNextInstallment: String?,
    @SerializedName("next_installment_amount") val nextInstallmentAmount: Double?
)