package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.BuildConfig

object NetworkConfig {
    /**
     * Override this locally through `local.properties` with:
     * `schoolbridge.apiBaseUrl=http://<your-machine-ip>:8080`
     * so physical devices can reach your local backend without hardcoding it in git.
     */
    val apiBaseUrl: String = BuildConfig.API_BASE_URL
}
