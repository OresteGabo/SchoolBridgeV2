package com.schoolbridge.v2.data.dto.finance

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Payment Method record**.
 *
 * This DTO defines a specific method by which payments can be made (e.g., "Cash," "Bank Transfer," "Credit Card")
 * as it is exchanged with the backend API. It helps categorize and manage payment options.
 *
 * **When to use this class:**
 * You'll use `PaymentMethodDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a list of available payment methods (e.g., for parents to select when making a payment).
 * 2.  **Configuring payment options:** When managing the different ways payments can be accepted by the institution.
 *
 * **How to use it:**
 * `PaymentMethodDto` objects are typically received from your API layer and then **mapped** to your domain `PaymentMethod` model
 * (likely an enum or a sealed class) for type-safe usage in your application, allowing for specific logic per method.
 *
 * **Real-life Example:**
 * -   A parent initiates a payment and chooses "MOBILE_MONEY" from the available options.
 * -   The system generates a report showing payments categorized by "CASH" vs. "ONLINE."
 *
 * @property id A unique identifier for the payment method. Example: "PM_MOBILE_MONEY"
 * @property name The display name of the payment method. Example: "Mobile Money"
 * @property code An optional short code or abbreviation for the method. Nullable. Example: "MOMO"
 * @property description A brief description of the payment method, if available. Nullable. Example: "Payments via mobile money services."
 * @property isOnline A boolean indicating if this payment method is handled online (e.g., via a payment gateway). Example: `true`
 */
data class PaymentMethodDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("isOnline") val isOnline: Boolean
)