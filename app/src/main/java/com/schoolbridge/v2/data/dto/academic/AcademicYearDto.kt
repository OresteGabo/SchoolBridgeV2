package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an **Academic Year record**.
 *
 * This DTO represents a specific academic year (e.g., "2024-2025") as it is exchanged with the backend API.
 * It defines the start and end dates of the academic period and can include a descriptive name.
 *
 * **When to use this class:**
 * You'll use `AcademicYearDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a list of available academic years (e.g., for filtering student records or setting up new courses).
 * 2.  **Configuring settings:** When defining the current or future academic periods in the system.
 *
 * **How to use it:**
 * `AcademicYearDto` objects are typically received from your API layer and then **mapped** to your domain `AcademicYear` model for comprehensive use in your application's business logic and UI (e.g., to display human-readable dates or apply academic year-specific rules).
 *
 * **Real-life Example:**
 * -   A school administrator defines the "2024-2025 Academic Year" which runs from September 1, 2024, to June 30, 2025.
 * -   A student views their historical grades, filtered by "2023-2024 Academic Year."
 *
 * @property id A unique identifier for the academic year. Example: "AY2024-2025"
 * @property name The display name of the academic year. Example: "2024-2025 Academic Year"
 * @property startDate The start date of the academic year, as an ISO 8601 date string. Example: "2024-09-01"
 * @property endDate The end date of the academic year, as an ISO 8601 date string. Example: "2025-06-30"
 */
data class AcademicYearDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String
)