// TestCourses.kt

package com.schoolbridge.v2.ideatrials

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.data.dto.academic.CourseDto
import com.schoolbridge.v2.data.remote.BASE_URL
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException


// -------------------------
// ✅ API Service
// -------------------------

class CourseService(
    private val client: HttpClient,
    private val token: String
)
{
    suspend fun fetchCourses(): List<CourseDto> {
        Log.d("CourseService", "Fetching courses of a enrolled student...")
        return try {
            //update this to be courses of an enrolled student instead of all courses
            val response = client.get("$BASE_URL/api/courses") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            Log.d("CourseService", response.body())
            Log.d("CourseService", "Response status: ${response.status}")

            response.body()

        } catch (e: ClientRequestException) {
            Log.e("CourseService", "Client error: ${e.response.status}", e)
            throw Exception("Client error: ${e.response.status}")
        } catch (e: ServerResponseException) {
            Log.e("CourseService", "Server error: ${e.response.status}", e)
            throw Exception("Server error: ${e.response.status}")
        } catch (e: IOException) {
            Log.e("CourseService", "Network error", e)
            throw IOException("Network error", e)
        } catch (e: Exception) {
            Log.e("CourseService", "Unexpected error: ${e.message}", e)
            throw Exception("Unexpected error: ${e.message}")
        }
    }
}


// -------------------------
// ✅ Repository
// -------------------------

class CourseRepository(private val service: CourseService) {
    suspend fun getAllCourses(): List<CourseDto> = service.fetchCourses()
}

// -------------------------
// ✅ ViewModel
// -------------------------

class CoursesViewModel(
    private val repository: CourseRepository
) : ViewModel()
{

    var Courses by mutableStateOf<List<CourseDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        Log.d("CoursesViewModel", "Initializing and loading Courses")
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d("CoursesViewModel", "Fetching Courses...")
                Courses = repository.getAllCourses()
                Log.d("CoursesViewModel", "Fetched ${Courses.size} Courses")
                error = null
            } catch (e: Exception) {
                Log.e("CoursesViewModel", "Failed to load Courses: ${e.message}", e)
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}


// -------------------------
// ✅ ViewModel Factory
// -------------------------

class CoursesViewModelFactory(private val context: Context,private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("CourseVMFactory", "Creating ViewModel with context: $context")

        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; prettyPrint = true })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }

        //val sessionManager = UserSessionManager(context)
        val service = CourseService(client, token)
        val repo = CourseRepository(service)

        return CoursesViewModel(repo) as T
    }
}

// -------------------------
// ✅ Composable
// -------------------------


@Composable
fun CoursesScreen(context: Context, token: String) {
    val viewModel: CoursesViewModel = viewModel(
        factory = CoursesViewModelFactory(token = token, context = context)
    )

    val courses = viewModel.Courses
    val loading = viewModel.isLoading
    val error = viewModel.error

    Column{
        when {
            loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            error != null -> {
                Text("Error: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            else -> {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(courses) { course ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("📍 ${course.name}", style = MaterialTheme.typography.titleMedium)
                                Text("Subject: ${course.name}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

}
