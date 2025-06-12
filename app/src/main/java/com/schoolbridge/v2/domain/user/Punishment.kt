package com.schoolbridge.v2.domain.user

import java.time.LocalDate



// File: com.schoolvridge.v2.domain.model.Punishment.kt (or similar path)
/**
 * Represents a **Punishment record** within the application's domain logic.
 *
 * This class is the client-side's "source of truth" for a disciplinary action. It uses
 * type-safe enums and date objects, making it easier and safer to work with in the UI
 * and business logic compared to raw strings from the DTO.
 *
 * **When to use this class:**
 * You'll use `Punishment` objects in your ViewModels, UI components, and any other
 * business logic that needs to display, filter, or process punishment information.
 *
 * **How it works:**
 * Typically, an API layer (e.g., a repository or use case) will receive `PunishmentDto`s
 * from the backend, convert them into `Punishment` domain objects, and then pass these
 * domain objects to the rest of the application.
 *
 * **Example Situations:**
 * -   **Displaying student disciplinary history:** A list of `Punishment` objects is shown to a student or parent.
 * -   **Applying business rules:** Checking if a student is currently under `SUSPENSION` based on `type` and `durationDays`.
 * -   **Parent acknowledgement:** Updating the `acknowledgedByParent` status after a parent interacts with the UI.
 *
 * @property id Unique identifier for the punishment record.
 * @property type The specific type of punishment, using the type-safe [PunishmentType] enum.
 * @property reason A detailed explanation for why the punishment was issued.
 * @property dateIssued The exact date the punishment was issued, as a [LocalDate] object.
 * @property durationDays The duration of the punishment in days, if applicable (e.g., for suspension).
 * Nullable for warnings or single-event punishments.
 * @property teacherId The ID of the teacher who issued the punishment. Nullable if issued by an administrator.
 * @property acknowledgedByParent A boolean indicating if a parent/guardian has acknowledged this punishment.
 */
data class Punishment(
    val id: String,
    val type: PunishmentType,
    val reason: String,
    val dateIssued: LocalDate,
    val durationDays: Int?,
    val teacherId: String?,
    val acknowledgedByParent: Boolean
)