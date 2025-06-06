package com.schoolbridge.v2.domain.common

/**
 * Enum defining the possible types of votes a user can cast on a [Votable] item.
 *
 * @property UPVOTE A positive vote, typically indicating approval or agreement.
 * @property DOWNVOTE A negative vote, typically indicating disapproval or disagreement.
 */
enum class VoteType {
    UPVOTE,
    DOWNVOTE
}