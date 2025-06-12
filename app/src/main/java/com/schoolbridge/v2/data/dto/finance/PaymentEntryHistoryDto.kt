package com.schoolbridge.v2.data.dto.finance

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Payment Entry History record**.
 *
 * This DTO represents a single record of a payment made by or on behalf of a student,
 * as it is exchanged with the backend API. It captures details specific to the act of recording a payment.
 *
 * **When to use this class:**
 * You'll use `PaymentEntryHistoryDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a list of a student's payment history (e.g., to show parents all installments paid).
 * 2.  **Auditing payments:** For financial reconciliation and tracking individual payment events.
 *
 * **How to use it:**
 * `PaymentEntryHistoryDto` objects are typically received from your API layer and then **mapped** to your domain
 * `PaymentEntryHistory` model for comprehensive use in your application's financial logic and UI (e.g., displaying
 * transaction details or calculating remaining balances).
 *
 * **Real-life Example:**
 * -   A parent checks their child's account and sees a list of payments made, including amounts and dates.
 * -   An administrator logs a new cash payment received from a student.
 *
 * @property id A unique identifier for this specific payment entry. Example: "PAYENT001"
 * @property studentId The ID of the student associated with this payment. Example: "STUD001"
 * @property academicYearId The ID of the academic year this payment applies to, if applicable. Nullable. Example: "AY2024-2025"
 * @property amountPaid The amount of money paid in this entry. Example: 500.00
 * @property currency The currency of the payment, as an ISO 4217 code. Example: "USD", "RWF"
 * @property paymentDate The date the payment was made or recorded, as an ISO 8601 date string. Example: "2025-05-30"
 * @property paymentMethodId The ID of the [PaymentMethodDto] used for this payment. Example: "PM_BANK_TRANSFER"
 * @property transactionId The ID of the overarching [TransactionDto] if this payment is part of a larger transaction. Nullable. Example: "TRXN005"
 * @property receivedByUserId The ID of the user (e.g., school admin) who received or recorded this payment. Nullable. Example: "USER_ADMIN001"
 * @property notes Any additional notes or comments about the payment. Nullable. Example: "Partial tuition payment for Term 2."
 */
data class PaymentEntryHistoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("studentId") val studentId: String,
    @SerializedName("academicYearId") val academicYearId: String?,
    @SerializedName("amountPaid") val amountPaid: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("paymentDate") val paymentDate: String,
    @SerializedName("paymentMethodId") val paymentMethodId: String,
    @SerializedName("transactionId") val transactionId: String?,
    @SerializedName("receivedByUserId") val receivedByUserId: String?,
    @SerializedName("notes") val notes: String?
)