plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    // *** CRITICAL: Apply the Hilt plugin (no version here) ***
    //id("com.google.dagger.hilt.android")

    // *** CRITICAL: Apply the KSP plugin (no version here) ***
    //id("com.google.devtools.ksp")
}

android {
    namespace = "com.schoolbridge.v2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.schoolbridge.v2"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        // IMPORTANT: Ensure this version is compatible with your Compose BOM and Kotlin version.
        // Check https://developer.android.com/jetpack/compose/setup#kotlin-version for compatible versions.
        // For Compose BOM 2024.05.00 (implied by targetSdk 35 and current date),
        // a common compatible Kotlin Compiler Extension Version is 1.5.11 or newer.
        kotlinCompilerExtensionVersion = "1.5.11" // <--- VERIFY THIS!
    }
    packaging {
        // Exclude specific files from the APK that can cause issues with different build tools
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
// --- Core Android & Compose Basics ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Compose BOM (Bill of Materials) ---
    // Manages compatible versions of Compose libraries automatically.
    // Ensure the version in your libs.versions.toml for 'androidx.compose.bom' is up-to-date
    // (e.g., '2024.05.00' or later as of current date).
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Also for Compose UI tests

    // --- Compose UI Toolkit ---
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview) // For @Preview annotations
    debugImplementation(libs.androidx.ui.tooling) // For interactive previews in Android Studio
    debugImplementation(libs.androidx.ui.test.manifest) // Required for UI tests in debug builds

    // --- Material 3 (Core) ---
    // Includes the fundamental Material Design 3 components.
    implementation(libs.androidx.material3)

    // --- Material Icons Extended (all icons) ---
    // Provides access to the full suite of Material Icons (Filled, Outlined, Rounded, Sharp, TwoTone).
    // This is implicitly versioned by the Compose BOM.
    implementation("androidx.compose.material:material-icons-extended") // Added

    // --- Material 3 Window Size Class (for adaptive layouts) ---
    // Helps build responsive UIs that adapt to different screen sizes and orientations.
    // This is implicitly versioned by the Compose BOM.
    implementation("androidx.compose.material3:material3-window-size-class") // Added

    // --- Navigation for Compose ---
    // Essential for managing screen navigation in Jetpack Compose.
    implementation("androidx.navigation:navigation-compose:2.7.0") // Added - Check for latest stable: 2.7.x or newer

    // --- JSON Serialization ---
    implementation("com.google.code.gson:gson:2.10.1") // Your existing Gson dependency

    // --- Testing Dependencies ---
    testImplementation(libs.junit) // Standard JUnit for unit tests
    androidTestImplementation(libs.androidx.junit) // JUnit extensions for Android instrumented tests
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI automation tests
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose UI testing specific JUnit rules

    // Retrofit core
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Converter for JSON (e.g., Gson)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

/*
    // Ktor client for network requests
    implementation("io.ktor:ktor-client-core:2.3.11") // Use the latest stable version
    implementation("io.ktor:ktor-client-android:2.3.11") // Android engine
    implementation("io.ktor:ktor-client-content-negotiation:2.3.11") // For JSON serialization/deserialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11") // Kotlinx.serialization for JSON

// For ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0") // Or your current version
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
*/

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    implementation("io.ktor:ktor-client-logging:2.3.11")
    implementation("io.ktor:ktor-client-cio:2.3.11") // Or whichever engine you use
    implementation("io.ktor:ktor-client-okhttp:2.3.11")
    implementation("io.ktor:ktor-client-auth:2.3.11")
    implementation("io.ktor:ktor-client-resources:2.3.11")
    implementation("io.ktor:ktor-client-websockets:2.3.11")
    implementation("io.ktor:ktor-client-json:2.3.11")
    implementation("io.ktor:ktor-client-serialization:2.3.11") // Make sure this is present

    // For JsonNamingStrategy.SnakeCase (should be part of core kotlinx.serialization-json)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")


    // --- DataStore Preferences ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Hilt (Dependency Injection) ---
    implementation("com.google.dagger:hilt-android:2.51.1")

    // *** CRITICAL: Use 'ksp' for Hilt Annotation Processors ***
    // These lines are what require the 'ksp' plugin to be applied above.
    //ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    //ksp("androidx.hilt:hilt-compiler:1.2.0")

    // --- WorkManager Runtime ---
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    implementation ("androidx.datastore:datastore-preferences:1.1.7")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1" ) // for lifecycleScope in Activities/Fragments


    // --- Testing Dependencies ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.google.zxing:core:3.5.3")

    implementation(platform("androidx.compose:compose-bom:2024.06.00")) // Use the latest BOM version
    implementation("androidx.compose.ui:ui-text-google-fonts")

    implementation("com.google.android.gms:play-services-base:18.4.0") // Use the latest stable version
    // or if you only need the fonts provider specifically:
    // implementation("com.google.android.gms:play-services-fido:19.0.1") // This module specifically includes the font provider

    // And don't forget your Compose UI Google Fonts dependency
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.8")
// Use the latest stable version
    // Or if you only need the fonts provider specifically (though base is often sufficient)
    implementation("com.google.android.gms:play-services-fido:19.0.1") // Or the latest stable version

    implementation("com.google.android.gms:play-services-base:18.4.0") // Use the latest stable version
    // Or if you only need the fonts provider specifically (though base is often sufficient)
    // implementation("com.google.android.gms:play-services-fido:19.0.1") // Or the latest stable version

    // Also ensure you have the Compose UI Google Fonts dependency
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.8")

    implementation ("com.google.accompanist:accompanist-swiperefresh:<latest-version>")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.27.0")

}