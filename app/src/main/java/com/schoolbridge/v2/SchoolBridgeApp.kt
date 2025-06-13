// src/main/java/com/schoolbridge/v2/SchoolBridgeApp.kt (Create this if you don't have one)
package com.schoolbridge.v2

import android.app.Application
import com.schoolbridge.v2.data.remote.AuthApiServiceImpl // Your API service impl
import com.schoolbridge.v2.data.session.UserSessionManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@HiltAndroidApp
class SchoolBridgeApp : Application() {

    // You can make these accessible via a simple getter or integrate with DI frameworks
    // For now, making them accessible directly for demonstration.
    val authApiService by lazy { AuthApiServiceImpl() }
    val userSessionManager by lazy { UserSessionManager(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        // Initialize the session manager when the app starts
        CoroutineScope(Dispatchers.IO).launch {
            userSessionManager.initializeSession()
        }
    }
}