package com.schoolbridge.v2.domain.academic

/**
 * Represents a general academic Subject (e.g., Mathematics, History, Biology).
 * This is the high-level category under which courses are taught.
 *
 * @property id Unique identifier for the subject.
 * @property name The name of the subject (e.g., "Mathematics", "Biology", "Literature").
 * @property description A brief description of the subject area.
 *
 * Example Usage:
 * val mathSubject = Subject(
 * id = "subj-math",
 * name = "Mathematics",
 * description = "The study of numbers, quantities, and shapes."
 * )
 */
data class Subject(
    val id: String,
    val name: String,
    val description: String?
)