package com.schoolbridge.v2.ui.message

import android.content.Context

data class MessageInboxUiPrefs(
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: MessageInboxFilter = MessageInboxFilter.ALL
)

object MessageInboxPreferences {
    private const val PREFS_NAME = "message_inbox_prefs"
    private const val KEY_PINNED = "pinned_conversations"
    private const val KEY_MUTED = "muted_conversations"
    private const val KEY_ARCHIVED = "archived_conversations"
    private const val KEY_SEARCH_VISIBLE = "search_visible"
    private const val KEY_SEARCH_QUERY = "search_query"
    private const val KEY_FILTER = "selected_filter"
    private const val KEY_STARRED_PREFIX = "starred_messages"

    fun getInboxUiState(context: Context, userId: String?): MessageInboxUiPrefs {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val searchVisibleKey = scopedKey(KEY_SEARCH_VISIBLE, userId)
        val searchQueryKey = scopedKey(KEY_SEARCH_QUERY, userId)
        val filterKey = scopedKey(KEY_FILTER, userId)
        return MessageInboxUiPrefs(
            isSearchVisible = prefs.getBoolean(searchVisibleKey, false),
            searchQuery = prefs.getString(searchQueryKey, "").orEmpty(),
            selectedFilter = prefs.getString(filterKey, MessageInboxFilter.ALL.name)
                ?.let { raw -> MessageInboxFilter.entries.firstOrNull { it.name == raw } }
                ?: MessageInboxFilter.ALL
        )
    }

    fun saveInboxUiState(context: Context, userId: String?, state: MessageInboxUiPrefs) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(scopedKey(KEY_SEARCH_VISIBLE, userId), state.isSearchVisible)
            .putString(scopedKey(KEY_SEARCH_QUERY, userId), state.searchQuery)
            .putString(scopedKey(KEY_FILTER, userId), state.selectedFilter.name)
            .apply()
    }

    fun getPinnedConversationIds(context: Context, userId: String?): Set<String> =
        getStringSet(context, userId, KEY_PINNED)

    fun togglePinnedConversation(context: Context, userId: String?, conversationId: String): Set<String> =
        toggleStringSetItem(context, userId, KEY_PINNED, conversationId)

    fun getMutedConversationIds(context: Context, userId: String?): Set<String> =
        getStringSet(context, userId, KEY_MUTED)

    fun toggleMutedConversation(context: Context, userId: String?, conversationId: String): Set<String> =
        toggleStringSetItem(context, userId, KEY_MUTED, conversationId)

    fun getArchivedConversationIds(context: Context, userId: String?): Set<String> =
        getStringSet(context, userId, KEY_ARCHIVED)

    fun toggleArchivedConversation(context: Context, userId: String?, conversationId: String): Set<String> =
        toggleStringSetItem(context, userId, KEY_ARCHIVED, conversationId)

    fun getStarredMessageIds(context: Context, userId: String?, conversationId: String): Set<String> =
        getStringSet(context, userId, starredKey(conversationId))

    fun toggleStarredMessage(
        context: Context,
        userId: String?,
        conversationId: String,
        messageId: String
    ): Set<String> = toggleStringSetItem(context, userId, starredKey(conversationId), messageId)

    private fun getStringSet(context: Context, userId: String?, key: String): Set<String> =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(scopedKey(key, userId), emptySet())
            ?.toSet()
            .orEmpty()

    private fun toggleStringSetItem(
        context: Context,
        userId: String?,
        key: String,
        value: String
    ): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val scopedKey = scopedKey(key, userId)
        val updated = prefs.getStringSet(scopedKey, emptySet()).orEmpty().toMutableSet()
        if (!updated.add(value)) {
            updated.remove(value)
        }
        prefs.edit().putStringSet(scopedKey, updated).apply()
        return updated.toSet()
    }

    private fun scopedKey(base: String, userId: String?): String =
        if (userId.isNullOrBlank()) base else "${base}_$userId"

    private fun starredKey(conversationId: String): String = "${KEY_STARRED_PREFIX}_$conversationId"
}
