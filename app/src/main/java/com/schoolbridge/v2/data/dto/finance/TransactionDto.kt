package com.schoolbridge.v2.data.dto.finance

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Transaction record**.
 *
 * This DTO represents a general financial transaction within the school system, encompassing
 * various types like payments, refunds, or charges, as it is exchanged with the backend API.
 * It provides a comprehensive view of a financial event.
 *
 * **When to use this class:**
 * You'll use `TransactionDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a log of financial transactions (e.g., for detailed financial reporting or auditing).
 * 2.  **Sending data to the API:** Your app initiates or records a new financial transaction (e.g., applying a fee waiver or processing a refund).
 *
 * **How to use it:**
 * `TransactionDto` objects are typically received from your API layer and then **mapped** to your domain `Transaction` model
 * for use in your application's financial management, reporting, and auditing capabilities.
 *
 * **Real-life Example:**
 * -   A finance officer reviews a list of all transactions for the month, seeing both payments and refunds.
 * -   The system automatically generates a transaction record for a new tuition fee applied.
 *
 * @property id A unique identifier for the transaction. Example: "TRXN001"
 * @property type The type of transaction. Example: "PAYMENT", "REFUND", "CHARGE", "WAIVER"
 * @property amount The monetary amount of the transaction. Example: 1500.00 (for charge/payment), -50.00 (for refund)
 * @property currency The currency of the transaction, as an ISO 4217 code. Example: "USD", "RWF"
 * @property transactionDate The date and time the transaction occurred, as an ISO 8601 datetime string. Example: "2025-05-30T10:30:00Z"
 * @property description A brief description of the transaction. Nullable. Example: "First tuition installment."
 * @property paymentMethodId The ID of the [PaymentMethodDto] used for this transaction, if applicable. Nullable. Example: "PM_CREDIT_CARD"
 * @property referenceNumber A unique reference number for the transaction (e.g., bank reference, payment gateway ID). Nullable. Example: "BANKREF7890"
 * @property status The current status of the transaction. Example: "COMPLETED", "PENDING", "FAILED", "CANCELLED"
 * @property relatedEntityId The ID of the primary entity (e.g., student, school, vendor) that this transaction relates to. Nullable.
 * @property relatedEntityType The type of the related entity (e.g., "STUDENT", "SCHOOL", "VENDOR"). Nullable.
 */
data class TransactionDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("transaction_date") val transactionDate: String,
    @SerializedName("description") val description: String?,
    @SerializedName("payment_method_id") val paymentMethodId: String?,
    @SerializedName("reference_number") val referenceNumber: String?,
    @SerializedName("status") val status: String,
    @SerializedName("related_entity_id") val relatedEntityId: String?,
    @SerializedName("related_entity_type") val relatedEntityType: String?
)