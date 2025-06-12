package com.schoolbridge.v2.domain.academic

/**
 * Represents a Chapter within a [Course].
 * Chapters are logical subdivisions of course content.
 *
 * @property id Unique identifier for the chapter.
 * @property title The title of the chapter (e.g., "Chapter 1: Introduction to Biology").
 * @property description A brief description of the chapter's content.
 * @property courseId The ID of the [Course] to which this chapter belongs.
 * @property chapterNumber The sequential number of the chapter within its course (e.g., 1, 2, 3).
 * @property order The display order of the chapter, useful if [chapterNumber] isn't strictly sequential.
 *
 * Example Usage:
 * val biologyChapter = Chapter(
 * id = "bio-chap-1",
 * title = "Introduction to Cells",
 * description = "Covers the basic structure and function of cells.",
 * courseId = "bio-101",
 * chapterNumber = 1,
 * order = 1
 * )
 */
data class Chapter(
    val id: String,
    val title: String,
    val description: String?,
    val courseId: String,
    val chapterNumber: Int,
    val order: Int
)