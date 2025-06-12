package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Punishment record**.
 *
 * This DTO represents a disciplinary action or punishment issued to a student or staff member
 * as it is exchanged with the backend API. It encapsulates details about the nature of the
 * punishment, its reason, and when it was issued.
 *
 * **When to use this class:**
 * You'll use `PunishmentDto` primarily when:
 * 1.  **Fetching data from the API:** The backend sends a list of punishment records (e.g., for a student's disciplinary history), which your app deserializes into `PunishmentDto` objects.
 * 2.  **Sending data to the API:** Your app sends a new punishment record (e.g., a teacher issues a detention) to the backend.
 *
 * **How to use it:**
 * Typically, you'll receive `PunishmentDto` objects from your API layer. These DTOs are then
 * often **mapped** to your domain `Punishment` model for use in your application's business logic and UI.
 *
 * **Real-life Example:**
 * -   A teacher might issue a "detention" to a student for late homework. The app creates a `PunishmentDto`
 * (e.g., `PunishmentDto(id="new", type="DETENTION", reason="Late homework", dateIssued="2024-06-05", durationDays=null, teacherId="TCHR001", acknowledgedByParent=false)`)
 * and sends it to the backend.
 * -   When viewing a student's disciplinary record, the backend returns a list of `PunishmentDto`s,
 * which your app converts into `Punishment` domain objects for display.
 *
 * @property id A unique identifier for the punishment record as provided by the backend. Example: "PUN00123"
 * @property type The type of punishment issued, represented as a **string** matching a backend enumeration.
 * Example: "DETENTION", "SUSPENSION", "WARNING". This maps directly to a `PunishmentType` enum in your domain model.
 * @property reason A detailed explanation for why the punishment was issued.
 * Example: "Repeated failure to submit homework on time."
 * @property dateIssued The date the punishment was issued, as an **ISO 8601 date string**.
 * Example: "2024-05-30". This will be converted to a `LocalDate` in your domain model.
 * @property durationDays The duration of the punishment in days, if applicable (e.g., for suspension).
 * Nullable for warnings or single-event punishments. Example: 3 (for a 3-day suspension).
 * @property teacherId The unique ID of the teacher who issued the punishment. Nullable if issued by an administrator.
 * Example: "TCHR001"
 * @property acknowledgedByParent A boolean indicating if a parent/guardian has acknowledged this
 * punishment. This might be a workflow step in the system (e.g., a "seen" button in the parent app).
 * Example: `true` if the parent has seen and acknowledged it.
 */
data class PunishmentDto(
    val id: String,
    val type: String,
    val reason: String,
    @SerializedName("dateIssued") val dateIssued: String,
    @SerializedName("durationDays") val durationDays: Int?,
    @SerializedName("teacherId") val teacherId: String?,
    @SerializedName("acknowledgedByParent") val acknowledgedByParent: Boolean
)