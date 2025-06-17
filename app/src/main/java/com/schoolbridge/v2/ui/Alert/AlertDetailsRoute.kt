package com.schoolbridge.v2.ui.Alert

import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertType
import java.time.LocalDateTime




open class AlertRepository {

    open fun getAlerts(): List<Alert> {
        return listOf(
            Alert(
                id = "1",
                title = "System Maintenance",
                message = "The system will be down for maintenance on June 20th from 12:00 AM to 4:00 AM.",
                timestamp = LocalDateTime.of(2025, 6, 18, 14, 30),
                isRead = false,
                type = AlertType.WARNING
            ),
            Alert(
                id = "2",
                title = "New Feature Released",
                message = "We have launched the new messaging feature. Check it out!",
                timestamp = LocalDateTime.of(2025, 6, 17, 10, 0),
                isRead = true,
                type = AlertType.INFO
            ),
            Alert(
                id = "3",
                title = "Password Expiry Reminder",
                message = "Your password will expire in 5 days. Please update it to avoid login issues.",
                timestamp = LocalDateTime.of(2025, 6, 16, 9, 0),
                isRead = false,
                type = AlertType.WARNING
            ),
            Alert(
                id = "4",
                title = "Congratulations!",
                message = "You have successfully linked a new child to your account.",
                timestamp = LocalDateTime.of(2025, 6, 15, 16, 45),
                isRead = true,
                type = AlertType.SUCCESS
            ),
            Alert(
                id = "5",
                title = "Scheduled School Event",
                message = "Don't forget the school sports day on July 1st at 9 AM.",
                timestamp = LocalDateTime.of(2025, 6, 14, 11, 30),
                isRead = false,
                type = AlertType.INFO
            ),
            Alert(
                id = "6",
                title = "Urgent: Update Required",
                message = "Please update your profile information before June 25th to avoid disruptions.",
                timestamp = LocalDateTime.of(2025, 6, 13, 8, 15),
                isRead = false,
                type = AlertType.ERROR
            ),
            Alert(
                id = "7",
                title = "Holiday Announcement",
                message = "School will be closed on June 30th for public holiday.",
                timestamp = LocalDateTime.of(2025, 6, 12, 12, 0),
                isRead = true,
                type = AlertType.INFO
            ),
            Alert(
                id = "8",
                title = "Reminder: Submit Forms",
                message = "Please submit the permission forms for the upcoming field trip by June 20th.",
                timestamp = LocalDateTime.of(2025, 6, 11, 14, 0),
                isRead = false,
                type = AlertType.WARNING
            ),
            Alert(
                id = "9",
                title = "Security Alert",
                message = "Multiple login attempts detected on your account. If this wasn't you, reset your password immediately.",
                timestamp = LocalDateTime.of(2025, 6, 10, 20, 45),
                isRead = false,
                type = AlertType.ERROR
            ),
            Alert(
                id = "10",
                title = "Weekly Newsletter",
                message = "Check out this week's newsletter for school updates and announcements.",
                timestamp = LocalDateTime.of(2025, 6, 9, 9, 0),
                isRead = true,
                type = AlertType.INFO
            ),
            Alert(
                id = "11",
                title = "Profile Verified",
                message = "Your profile has been successfully verified by the school administration.",
                timestamp = LocalDateTime.of(2025, 6, 8, 16, 10),
                isRead = true,
                type = AlertType.SUCCESS
            ),
            Alert(
                id = "12",
                title = "Event Cancellation",
                message = "The Parent-Teacher Meeting scheduled for July 10th has been canceled.",
                timestamp = LocalDateTime.of(2025, 6, 7, 13, 0),
                isRead = false,
                type = AlertType.ERROR
            ),
            Alert(
                id = "13",
                title = "System Update",
                message = "A new system update will be applied on June 22nd at 2 AM. Expect brief downtime.",
                timestamp = LocalDateTime.of(2025, 6, 6, 18, 30),
                isRead = true,
                type = AlertType.INFO
            ),
            Alert(
                id = "14",
                title = "Payment Reminder",
                message = "Your school fees for the term are due by July 5th. Please make payment on time.",
                timestamp = LocalDateTime.of(2025, 6, 5, 10, 45),
                isRead = false,
                type = AlertType.WARNING
            ),
            Alert(
                id = "15",
                title = "Feedback Request",
                message = "We value your feedback. Please take a moment to complete our survey.",
                timestamp = LocalDateTime.of(2025, 6, 4, 11, 15),
                isRead = true,
                type = AlertType.INFO
            )
        )
    }
}

