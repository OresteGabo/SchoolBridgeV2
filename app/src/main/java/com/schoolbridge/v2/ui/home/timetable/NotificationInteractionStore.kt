package com.schoolbridge.v2.ui.home.timetable

import android.content.Context

private const val STORE_NAME = "schedule_notification_interactions"
private const val KEY_HANDLED_ACTION_MESSAGES = "handled_action_messages"
private const val KEY_DECISION_PREFIX = "meeting_decision_"

enum class MeetingDecision {
    ATTENDING,
    DECLINED,
    MAYBE
}

object NotificationInteractionStore {
    fun markMessageHandled(context: Context, messageId: String) {
        val prefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
        val current = prefs.getStringSet(KEY_HANDLED_ACTION_MESSAGES, emptySet()).orEmpty().toMutableSet()
        current += messageId
        prefs.edit().putStringSet(KEY_HANDLED_ACTION_MESSAGES, current).apply()
    }

    fun isMessageHandled(context: Context, messageId: String): Boolean {
        val prefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_HANDLED_ACTION_MESSAGES, emptySet()).orEmpty().contains(messageId)
    }

    fun saveMeetingDecision(context: Context, threadId: String, decision: MeetingDecision) {
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("$KEY_DECISION_PREFIX$threadId", decision.name)
            .apply()
    }

    fun getMeetingDecision(context: Context, threadId: String): MeetingDecision? {
        val raw = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
            .getString("$KEY_DECISION_PREFIX$threadId", null)
            ?: return null
        return runCatching { MeetingDecision.valueOf(raw) }.getOrNull()
    }
}
