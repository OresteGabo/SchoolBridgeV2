
package com.schoolbridge.v2.ui

import android.util.Log
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertSeverity
import com.schoolbridge.v2.domain.messaging.AlertSourceType
import com.schoolbridge.v2.domain.messaging.AlertType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

class AlertRepository {

    /* ---------- internal mutable state ---------- */
    private val _alerts = MutableStateFlow(
        listOf(
            Alert(
                id = "1",
                title = "System Maintenance",
                message = "The system will be down for maintenance on June 20th from 12:00 AM to 4:00 AM.",
                timestamp = LocalDateTime.of(2025, 6, 18, 14, 30),
                type = AlertType.WARNING,
                publisherName = "IT Department",
                publisherType = AlertSourceType.SYSTEM,
                severity = AlertSeverity.MEDIUM,
                sourceOrganization = "SchoolBridge Platform"
            ),
            Alert(
                id = "2",
                title = "New Feature Released",
                message = "We have launched the new messaging feature. Check it out!",
                timestamp = LocalDateTime.of(2025, 6, 17, 10, 0),
                type = AlertType.INFO,
                publisherName = "SchoolBridge Team",
                publisherType = AlertSourceType.SYSTEM,
                severity = AlertSeverity.LOW,
                sourceOrganization = "SchoolBridge Platform"
            ),
            Alert(
                id = "3",
                title = "Password Expiry Reminder",
                message = "Your password will expire in 5 days. Please update it to avoid login issues.",
                timestamp = LocalDateTime.of(2025, 6, 16, 9, 0),
                type = AlertType.WARNING,
                publisherName = "School Admin",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.MEDIUM,
                sourceOrganization = "Green Valley High School"
            ),
            Alert(
                id = "4",
                title = "Congratulations!",
                message = "You have successfully linked a new child to your account.",
                timestamp = LocalDateTime.of(2025, 6, 15, 16, 45),
                type = AlertType.SUCCESS,
                publisherName = "SchoolBridge",
                publisherType = AlertSourceType.SYSTEM,
                severity = AlertSeverity.LOW,
                studentName = "Emmanuel N.",
                sourceOrganization = "SchoolBridge Platform"
            ),
            Alert(
                id = "5",
                title = "Scheduled School Event",
                message = "Don't forget the school sports day on July 1st at 9 AM.",
                timestamp = LocalDateTime.of(2025, 6, 14, 11, 30),
                type = AlertType.INFO,
                publisherName = "PE Department",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.LOW,
                sourceOrganization = "Riverdale Academy"
            ),
            Alert(
                id = "6",
                title = "Urgent: Update Required",
                message = "Please update your profile information before June 25th to avoid disruptions.",
                timestamp = LocalDateTime.of(2025, 6, 13, 8, 15),
                type = AlertType.ERROR,
                publisherName = "Registrar's Office",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.HIGH,
                sourceOrganization = "Maplewood School"
            ),
            Alert(
                id = "7",
                title = "Holiday Announcement",
                message = "School will be closed on June 30th for public holiday.",
                timestamp = LocalDateTime.of(2025, 6, 12, 12, 0),
                type = AlertType.INFO,
                publisherName = "School Director",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.LOW,
                sourceOrganization = "Oakridge International"
            ),
            Alert(
                id = "8",
                title = "Reminder: Submit Forms",
                message = "Please submit the permission forms for the upcoming field trip by June 20th.",
                timestamp = LocalDateTime.of(2025, 6, 11, 14, 0),
                type = AlertType.WARNING,
                publisherName = "Class Teacher",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.MEDIUM,
                studentName = "Claudine U.",
                sourceOrganization = "Westview Secondary School"
            ),
            Alert(
                id = "9",
                title = "Security Alert",
                message = "Multiple login attempts detected on your account. If this wasn't you, reset your password immediately.",
                timestamp = LocalDateTime.of(2025, 6, 10, 20, 45),
                type = AlertType.ERROR,
                publisherName = "Security System",
                publisherType = AlertSourceType.SYSTEM,
                severity = AlertSeverity.HIGH,
                sourceOrganization = "SchoolBridge Platform"
            ),
            Alert(
                id = "10",
                title = "Weekly Newsletter",
                message = "Check out this week's newsletter for school updates and announcements.",
                timestamp = LocalDateTime.of(2025, 6, 9, 9, 0),
                type = AlertType.INFO,
                publisherName = "Communications Office",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.LOW,
                sourceOrganization = "Pinecrest Academy"
            ),
            Alert(
                id = "11",
                title = "Profile Verified",
                message = "Your profile has been successfully verified by the school administration.",
                timestamp = LocalDateTime.of(2025, 6, 8, 16, 10),
                type = AlertType.SUCCESS,
                publisherName = "Registrar",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.LOW,
                sourceOrganization = "Hillside Preparatory"
            ),
            Alert(
                id = "12",
                title = "Event Cancellation",
                message = "The Parent-Teacher Meeting scheduled for July 10th has been canceled.",
                timestamp = LocalDateTime.of(2025, 6, 7, 13, 0),
                type = AlertType.ERROR,
                publisherName = "School Director",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.MEDIUM,
                sourceOrganization = "Cedar Grove School"
            ),
            Alert(
                id = "13",
                title = "System Update",
                message = "A new system update will be applied on June 22nd at 2 AM. Expect brief downtime.",
                timestamp = LocalDateTime.of(2025, 6, 6, 18, 30),
                type = AlertType.INFO,
                publisherName = "IT Department",
                publisherType = AlertSourceType.SYSTEM,
                severity = AlertSeverity.LOW,
                sourceOrganization = "SchoolBridge Platform"
            ),
            Alert(
                id = "14",
                title = "Payment Reminder",
                message = "Your school fees for the term are due by July 5th. Please make payment on time.",
                timestamp = LocalDateTime.of(2025, 6, 5, 10, 45),
                type = AlertType.WARNING,
                publisherName = "Finance Office",
                publisherType = AlertSourceType.SCHOOL,
                severity = AlertSeverity.HIGH,
                sourceOrganization = "Meadowbrook School"
            ),
            Alert(
                id = "15",
                title = "Feedback Request",
                message = "We value your feedback. Please take a moment to complete our survey.",
                timestamp = LocalDateTime.of(2025, 6, 4, 11, 15),
                type = AlertType.INFO,
                publisherName = "SchoolBridge Team",
                publisherType = AlertSourceType.SYSTEM,
                severity = AlertSeverity.LOW,
                sourceOrganization = "SchoolBridge Platform"
            )
        )
    )

    /* ---------- public read‑only view ---------- */
    val alerts: StateFlow<List<Alert>> = _alerts

    /* ---------- public actions ---------- */

    /** Mark every alert as read. */
    fun markAllAsRead() {

        _alerts.update { list ->                      // list is List<Alert>
            list.map { it.copy(isRead = true) }       // still List<Alert>
        }
        Log.d(TAG, "All alerts marked as read ")

    }

    /** Mark a single alert (by id) as read. */
    fun markAsRead(id: String) {
        _alerts.update { list ->
            list.map { alert ->
                if (alert.id == id) alert.copy(isRead = true) else alert
            }
        }
        Log.d(TAG, "Marked alert $id as read")
    }

    /** Mark a single alert (by id) as read. */
    fun markAsUnread(id: String) {
        _alerts.update { list ->
            list.map { alert ->
                if (alert.id == id) alert.copy(isRead = false) else alert
            }
        }
        Log.d(TAG, "Marked alert $id as unread")
    }

    companion object {
        private const val TAG = "AlertRepository"
    }

    fun displayAlertsIsRead(){
        _alerts.update { list ->
            list.map { alert ->
                alert.copy(isRead = true)
            }
        }
    }
}

/*
class AlertRepositorys {

    private val _alerts = MutableStateFlow(
        mutableListOf(
            Alert(
                id = "1",
                title = "System Maintenance",
                message = "The system will be down for maintenance on June 20th from 12:00 AM to 4:00 AM.",
                timestamp = LocalDateTime.of(2025, 6, 18, 14, 30),
                type = AlertType.WARNING
            ),
            Alert(
                id = "2",
                title = "New Feature Released",
                message = "We have launched the new messaging feature. Check it out!",
                timestamp = LocalDateTime.of(2025, 6, 17, 10, 0),
                type = AlertType.INFO
            ),
            Alert(
                id = "3",
                title = "Password Expiry Reminder",
                message = "Your password will expire in 5 days. Please update it to avoid login issues.",
                timestamp = LocalDateTime.of(2025, 6, 16, 9, 0),
                type = AlertType.WARNING
            ),
            Alert(
                id = "4",
                title = "Congratulations!",
                message = "You have successfully linked a new child to your account.",
                timestamp = LocalDateTime.of(2025, 6, 15, 16, 45),
                type = AlertType.SUCCESS
            ),
            Alert(
                id = "5",
                title = "Scheduled School Event",
                message = "Don't forget the school sports day on July 1st at 9 AM.",
                timestamp = LocalDateTime.of(2025, 6, 14, 11, 30),
                type = AlertType.INFO
            ),
            Alert(
                id = "6",
                title = "Urgent: Update Required",
                message = "Please update your profile information before June 25th to avoid disruptions.",
                timestamp = LocalDateTime.of(2025, 6, 13, 8, 15),
                type = AlertType.ERROR
            ),
            Alert(
                id = "7",
                title = "Holiday Announcement",
                message = "School will be closed on June 30th for public holiday.",
                timestamp = LocalDateTime.of(2025, 6, 12, 12, 0),
                type = AlertType.INFO
            ),
            Alert(
                id = "8",
                title = "Reminder: Submit Forms",
                message = "Please submit the permission forms for the upcoming field trip by June 20th.",
                timestamp = LocalDateTime.of(2025, 6, 11, 14, 0),
                type = AlertType.WARNING
            ),
            Alert(
                id = "9",
                title = "Security Alert",
                message = "Multiple login attempts detected on your account. If this wasn't you, reset your password immediately.",
                timestamp = LocalDateTime.of(2025, 6, 10, 20, 45),
                type = AlertType.ERROR
            ),
            Alert(
                id = "10",
                title = "Weekly Newsletter",
                message = "Check out this week's newsletter for school updates and announcements.",
                timestamp = LocalDateTime.of(2025, 6, 9, 9, 0),
                type = AlertType.INFO
            ),
            Alert(
                id = "11",
                title = "Profile Verified",
                message = "Your profile has been successfully verified by the school administration.",
                timestamp = LocalDateTime.of(2025, 6, 8, 16, 10),
                type = AlertType.SUCCESS
            ),
            Alert(
                id = "12",
                title = "Event Cancellation",
                message = "The Parent-Teacher Meeting scheduled for July 10th has been canceled.",
                timestamp = LocalDateTime.of(2025, 6, 7, 13, 0),
                type = AlertType.ERROR
            ),
            Alert(
                id = "13",
                title = "System Update",
                message = "A new system update will be applied on June 22nd at 2 AM. Expect brief downtime.",
                timestamp = LocalDateTime.of(2025, 6, 6, 18, 30),
                type = AlertType.INFO
            ),
            Alert(
                id = "14",
                title = "Payment Reminder",
                message = "Your school fees for the term are due by July 5th. Please make payment on time.",
                timestamp = LocalDateTime.of(2025, 6, 5, 10, 45),
                type = AlertType.WARNING
            ),
            Alert(
                id = "15",
                title = "Feedback Request",
                message = "We value your feedback. Please take a moment to complete our survey.",
                timestamp = LocalDateTime.of(2025, 6, 4, 11, 15),
                type = AlertType.INFO
            ))
    )
    val alerts: StateFlow<List<Alert>> = _alerts

    fun markAllAsRead() {
        _alerts.update { currentList ->
            currentList.map { it.copy(isRead = true) }
        }
        Log.d("AlertRepository", "All alerts marked as read")
    }

    fun markAsRead(id: String) {
        _alerts.update { currentList ->
            currentList.map {
                if (it.id == id) it.copy(isRead = true) else it
            }
        }
        Log.d("AlertRepository", "Marked alert $id as read")
    }
}
*/