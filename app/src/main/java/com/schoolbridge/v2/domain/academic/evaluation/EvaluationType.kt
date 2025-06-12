package com.schoolbridge.v2.domain.academic.evaluation

/**
 * Enum defining the possible types of an [Evaluation].
 *
 * @property EXAM A major comprehensive assessment.
 * @property QUIZ A short, quick assessment.
 * @property HOMEWORK An assignment to be completed outside of class.
 * @property PROJECT A longer-term, often collaborative, task.
 * @property PARTICIPATION Assessment based on class engagement.
 * @property OTHER A type not explicitly listed.
 */
enum class EvaluationType {
    EXAM,
    QUIZ,
    HOMEWORK,
    PROJECT,
    PARTICIPATION,
    OTHER
}