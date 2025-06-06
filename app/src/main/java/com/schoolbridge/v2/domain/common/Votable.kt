package com.schoolbridge.v2.domain.common

/**
 * An interface marking a domain entity as "votable".
 * Any entity that can receive upvotes or downvotes in the application should implement this interface.
 *
 * @property id The unique identifier of the votable item.
 * @property upvotes The current count of upvotes for this item.
 * @property downvotes The current count of downvotes for this item.
 * @property totalVotes A calculated property representing the net score (upvotes - downvotes).
 *
 * Example Usage (in a domain model like 'Post' or 'Comment'):
 * data class Post(
 * override val id: String,
 * val content: String,
 * override val upvotes: Int,
 * override val downvotes: Int
 * ) : Votable
 */
interface Votable {
    val id: String
    val upvotes: Int
    val downvotes: Int
    val totalVotes: Int
        get() = upvotes - downvotes
}