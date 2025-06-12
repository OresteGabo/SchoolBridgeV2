package com.schoolbridge.v2.domain.academic.evaluation

/**
 * Enum defining the possible formats for an [Evaluation].
 *
 * @property MULTIPLE_CHOICE An evaluation with multiple-choice questions.
 * @property ESSAY An evaluation requiring written essay responses.
 * @property PRACTICAL An evaluation involving hands-on tasks or experiments.
 * @property PRESENTATION An evaluation requiring a verbal presentation.
 * @property ORAL An evaluation based on spoken responses/questions.
 * @property OTHER A format not explicitly listed.
 */
enum class EvaluationFormat {
    MULTIPLE_CHOICE,
    ESSAY,
    PRACTICAL,
    PRESENTATION,
    ORAL,
    OTHER
}