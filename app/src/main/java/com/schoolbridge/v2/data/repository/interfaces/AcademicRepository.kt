package com.schoolbridge.v2.data.repository.interfaces

// You would need to import your DTOs here, e.g.:
// import com.schoolvridge.v2.data.dto.CourseDto
// import com.schoolvridge.v2.data.dto.ChapterDto
// import com.schoolvridge.v2.data.dto.ExerciseDto
// import com.schoolvridge.v2.data.dto.EvaluationDto
// import com.schoolvridge.v2.data.dto.SubjectDto
// import com.schoolvridge.v2.data.dto.common.AcademicYearDto // Assuming this exists

/**
 * Interface for the **Academic Data Repository**.
 *
 * This repository defines the contract for accessing and managing academic-related data,
 * such as subjects, courses, chapters, exercises, evaluations, and academic years.
 * It abstracts away the underlying data storage mechanisms.
 */
interface AcademicRepository {

    // Subjects
    suspend fun getSubjectById(subjectId: String): Any? // Replace Any with your SubjectDto
    suspend fun getAllSubjects(): List<Any> // Replace Any with your SubjectDto
    suspend fun createSubject(subject: Any): Any // Replace Any with your SubjectDto
    suspend fun updateSubject(subject: Any): Any // Replace Any with your SubjectDto
    suspend fun deleteSubject(subjectId: String)

    // Courses
    suspend fun getCourseById(courseId: String): Any? // Replace Any with your CourseDto
    suspend fun getCoursesBySubject(subjectId: String): List<Any> // Replace Any with your CourseDto
    suspend fun createCourse(course: Any): Any // Replace Any with your CourseDto
    suspend fun updateCourse(course: Any): Any // Replace Any with your CourseDto
    suspend fun deleteCourse(courseId: String)

    // Chapters
    suspend fun getChapterById(chapterId: String): Any? // Replace Any with your ChapterDto
    suspend fun getChaptersByCourse(courseId: String): List<Any> // Replace Any with your ChapterDto
    suspend fun createChapter(chapter: Any): Any // Replace Any with your ChapterDto
    suspend fun updateChapter(chapter: Any): Any // Replace Any with your ChapterDto
    suspend fun deleteChapter(chapterId: String)

    // Exercises
    suspend fun getExerciseById(exerciseId: String): Any? // Replace Any with your ExerciseDto
    suspend fun getExercisesByChapter(chapterId: String): List<Any> // Replace Any with your ExerciseDto
    suspend fun createExercise(exercise: Any): Any // Replace Any with your ExerciseDto
    suspend fun updateExercise(exercise: Any): Any // Replace Any with your ExerciseDto
    suspend fun deleteExercise(exerciseId: String)

    // Evaluations
    suspend fun getEvaluationById(evaluationId: String): Any? // Replace Any with your EvaluationDto
    suspend fun getEvaluationsBySubject(subjectId: String): List<Any> // Replace Any with your EvaluationDto
    suspend fun createEvaluation(evaluation: Any): Any // Replace Any with your EvaluationDto
    suspend fun updateEvaluation(evaluation: Any): Any // Replace Any with your EvaluationDto
    suspend fun deleteEvaluation(evaluationId: String)

    // Academic Years
    suspend fun getAcademicYearById(yearId: String): Any? // Replace Any with your AcademicYearDto
    suspend fun getAllAcademicYears(): List<Any> // Replace Any with your AcademicYearDto
}