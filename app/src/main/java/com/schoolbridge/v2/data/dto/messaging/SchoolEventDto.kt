package com.schoolbridge.v2.data.dto.messaging

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.messaging.attachments.InAppAttachmentDto

// import com.schoolvridge.v2.data.dto.common.InAppAttachmentDto // Ensure this path is correct

/**
 * Data Transfer Object (DTO) for a **School Event**.
 *
 * This DTO represents an event organized by the school (e.g., sports day, parent-teacher conference, holiday),
 * as it is exchanged with the backend API. It contains details necessary for display on calendars
 * or event lists.
 *
 * **When to use this class:**
 * You'll use `SchoolEventDto` primarily when:
 * 1.  **Displaying calendar events:** Populating a school calendar view for students, parents, and staff.
 * 2.  **Managing events:** An administrator creates, updates, or deletes school events.
 * 3.  **Providing event details:** When a user taps on an event to see more information.
 *
 * **How to use it:**
 * `SchoolEventDto` objects are received from your API layer and used to render event cards, calendar entries,
 * and detailed event pages. They can include `InAppAttachmentDto`s for links to event-related entities.
 *
 * **Real-life Example:**
 * -   The school calendar displays "Sports Day" with its date, time, and location.
 * -   An administrator adds a new "Parent-Teacher Conference" event to the system.
 * -   The event details page provides a map to the venue as an `InAppAttachmentDto`.
 *
 * @property id A unique identifier for this school event. Example: "EVENT_SPORTS_DAY_2025"
 * @property schoolId The ID of the school organizing this event. Example: "SCH001"
 * @property title The main title of the event. Example: "Annual Sports Day"
 * @property description A detailed description of the event. Example: "Join us for a day of fun and athletic competitions!"
 * @property type A string indicating the category of the event.
 * Examples: "SPORTS", "ACADEMIC", "HOLIDAY", "MEETING", "CULTURAL"
 * @property startDate The start date and time of the event, as an ISO 8601 datetime string. Example: "2025-06-08T09:00:00Z"
 * @property endDate The end date and time of the event, as an ISO 8601 datetime string. Example: "2025-06-08T16:00:00Z"
 * @property location The physical or virtual location where the event will take place. Example: "School Main Field"
 * @property targetAudience An optional string defining the target group for this event.
 * Examples: "ALL_STUDENTS", "ALL_PARENTS", "GRADE_7_STUDENTS". Nullable.
 * @property organizerUserId The ID of the user (e.g., staff member) who organized this event. Example: "USER_TEACHER005"
 * @property inAppAttachments A list of [InAppAttachmentDto] objects, representing previews/references
 * to other entities relevant to this event (e.g., a link to a detailed schedule, a map to the venue,
 * or related guidelines). Nullable.
 */
data class SchoolEventDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("location") val location: String,
    @SerializedName("targetAudience") val targetAudience: String?,
    @SerializedName("organizerUserId") val organizerUserId: String,
    @SerializedName("inAppAttachments") val inAppAttachments: List<InAppAttachmentDto>?
)