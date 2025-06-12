package com.schoolbridge.v2.domain.academic.evaluation

/**
 * Enum defining the possible formats for an [Exercise].
 *
 * @property WRITTEN An exercise requiring written submission.
 * @property ORAL An exercise involving spoken responses.
 * @property PRACTICAL An exercise involving hands-on tasks.
 * @property ONLINE_QUIZ An exercise conducted as an online quiz.
 * @property OTHER A format not explicitly listed.
 */
enum class ExerciseFormat {
    WRITTEN,
    ORAL,
    PRACTICAL,
    ONLINE_QUIZ,
    OTHER
}