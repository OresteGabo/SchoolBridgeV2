package com.schoolbridge.v2.domain.academic

/**
 * Represents an academic Combination or Stream (e.g., Science, Arts, Commercial).
 * This defines a specific grouping of subjects that students can pursue.
 *
 * @property id Unique identifier for the combination.
 * @property name The name of the combination (e.g., "Science Combination", "Arts Stream").
 * @property description A description of the combination's focus.
 * @property subjectIds A list of IDs of [Subject]s that typically form part of this combination.
 *
 * Example Usage:
 * val scienceCombination = Combination(
 * id = "combo-sci",
 * name = "Science Combination",
 * description = "Focuses on Physics, Chemistry, Biology.",
 * subjectIds = listOf("subj-phy", "subj-chem", "subj-bio")
 * )
 */
data class Combination(
    val id: String,
    val name: String,
    val description: String?,
    val subjectIds: List<String>
)