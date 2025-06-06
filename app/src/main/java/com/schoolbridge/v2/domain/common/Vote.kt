package com.schoolbridge.v2.domain.common

import java.time.LocalDateTime

/**
 * Represents a single vote cast by a user on a [Votable] item.
 * This domain model captures the details of an individual vote.
 *
 * @property id Unique identifier for the vote record.
 * @property userId The ID of the [User] who cast this vote.
 * @property votableItemId The ID of the [Votable] item that was voted on.
 * @property voteType The [VoteType] (e.g., [VoteType.UPVOTE], [VoteType.DOWNVOTE]) of this vote.
 * @property timestamp The date and time when the vote was cast.
 *
 * Example Usage:
 * val userUpvote = Vote(
 * id = "vote-123",
 * userId = "user-id-abc",
 * votableItemId = "post-id-xyz",
 * voteType = VoteType.UPVOTE,
 * timestamp = LocalDateTime.now()
 * )
 */
data class Vote(
    val id: String,
    val userId: String,
    val votableItemId: String,
    val voteType: VoteType,
    val timestamp: LocalDateTime
)