package com.schoolbridge.v2.data.dto.finance

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Bank Account record**.
 *
 * This DTO represents a bank account associated with the school or potentially a parent/guardian
 * for payment purposes, as it is exchanged with the backend API. It contains essential
 * details required to identify and interact with the account.
 *
 * **When to use this class:**
 * You'll use `BankAccountDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides details of bank accounts (e.g., school's official bank accounts for tuition payments).
 * 2.  **Sending data to the API:** Your app might submit new bank account details (e.g., a refund account for a student).
 *
 * **How to use it:**
 * `BankAccountDto` objects are typically received from your API layer and then **mapped** to your domain `BankAccount` model
 * for use in your application's business logic and UI (e.g., displaying account details for payments or managing school finances).
 *
 * **Real-life Example:**
 * -   The app displays the school's bank account number and name for parents to make bank transfers.
 * -   An administrator updates the banking details for a specific school branch.
 *
 * @property id A unique identifier for the bank account. Example: "BANK_ACC001"
 * @property accountName The name on the bank account. Example: "School Name Ltd."
 * @property accountNumber The bank account number. Example: "1234567890"
 * @property bankName The name of the bank. Example: "National Bank"
 * @property branchName The name of the bank branch, if applicable. Nullable. Example: "Main Street Branch"
 * @property currency The currency of the account, as an ISO 4217 code. Example: "USD", "RWF"
 * @property isDefault A boolean indicating if this is the default or primary bank account for its context. Nullable. Example: `true`
 */
data class BankAccountDto(
    @SerializedName("id") val id: String,
    @SerializedName("account_name") val accountName: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("bank_name") val bankName: String,
    @SerializedName("branch_name") val branchName: String?,
    @SerializedName("currency") val currency: String,
    @SerializedName("is_default") val isDefault: Boolean?
)