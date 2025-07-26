// TestDistricts.kt

package com.schoolbridge.v2.ideatrials

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.data.dto.geo.DistrictDto
import com.schoolbridge.v2.data.remote.BASE_URL
import com.schoolbridge.v2.data.session.UserSessionManager
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException


// -------------------------
// ✅ API Service
// -------------------------

class DistrictService(
    private val client: HttpClient,
    private val token: String
)
{
    suspend fun fetchDistricts(): List<DistrictDto> {
        Log.d("DistrictService", "Fetching districts...")
        return try {
            val response = client.get("$BASE_URL/api/districts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            Log.d("DistrictService", "Response status: ${response.status}")

            response.body()

        } catch (e: ClientRequestException) {
            Log.e("DistrictService", "Client error: ${e.response.status}", e)
            throw Exception("Client error: ${e.response.status}")
        } catch (e: ServerResponseException) {
            Log.e("DistrictService", "Server error: ${e.response.status}", e)
            throw Exception("Server error: ${e.response.status}")
        } catch (e: IOException) {
            Log.e("DistrictService", "Network error", e)
            throw IOException("Network error", e)
        } catch (e: Exception) {
            Log.e("DistrictService", "Unexpected error: ${e.message}", e)
            throw Exception("Unexpected error: ${e.message}")
        }
    }
}


// -------------------------
// ✅ Repository
// -------------------------

class DistrictRepository(private val service: DistrictService) {
    suspend fun getAllDistricts(): List<DistrictDto> = service.fetchDistricts()
}

// -------------------------
// ✅ ViewModel
// -------------------------

class DistrictsViewModel(
    private val repository: DistrictRepository
) : ViewModel()
{

    var districts by mutableStateOf<List<DistrictDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        Log.d("DistrictsViewModel", "Initializing and loading districts")
        loadDistricts()
    }

    private fun loadDistricts() {
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d("DistrictsViewModel", "Fetching districts...")
                districts = repository.getAllDistricts()
                Log.d("DistrictsViewModel", "Fetched ${districts.size} districts")
                error = null
            } catch (e: Exception) {
                Log.e("DistrictsViewModel", "Failed to load districts: ${e.message}", e)
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

class DistrictsViewModelFactory(private val context: Context,private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("DistrictVMFactory", "Creating ViewModel with context: $context")

        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; prettyPrint = true })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }

        //val sessionManager = UserSessionManager(context)
        val service = DistrictService(client, token)
        val repo = DistrictRepository(service)

        return DistrictsViewModel(repo) as T
    }
}

// -------------------------
// ✅ Composable
// -------------------------


@Composable
fun DistrictsScreen(context: Context, token: String) {
    val viewModel: DistrictsViewModel = viewModel(
        factory = DistrictsViewModelFactory(token = token, context = context)
    )

    val districts = viewModel.districts
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
                        .fillMaxHeight() // or .weight(1f) if inside another Column with other views
                ) {
                    items(districts) { district ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("📍 ${district.name}", style = MaterialTheme.typography.titleMedium)
                                Text("Sectors: ${district.sectorCount}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

            }
        }
    }

}
