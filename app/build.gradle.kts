plugins {
    // Versions come from libs.versions.toml
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    // Uncomment if/when you wire in DI + code-gen
    // id("com.google.dagger.hilt.android")
    // id("com.google.devtools.ksp")
}

fun readLocalProperty(name: String): String? {
    val file = rootProject.file("local.properties")
    if (!file.exists()) return null

    return file.readLines()
        .firstOrNull { line ->
            val trimmed = line.trim()
            trimmed.startsWith("$name=") && !trimmed.startsWith("#")
        }
        ?.substringAfter("=")
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
}

fun normalizeBuildConfigString(value: String): String =
    value
        .replace("\\:", ":")
        .replace("\\=", "=")
        .replace("\\\\", "\\")
        .replace("\"", "\\\"")

val schoolBridgeApiBaseUrl = (
    readLocalProperty("schoolbridge.apiBaseUrl")
        ?: System.getenv("SCHOOLBRIDGE_API_BASE_URL")
        ?: ""
).trim()

val releaseStoreFile = System.getenv("SCHOOLBRIDGE_UPLOAD_STORE_FILE")?.trim().orEmpty()
val releaseStorePassword = System.getenv("SCHOOLBRIDGE_UPLOAD_STORE_PASSWORD")?.trim().orEmpty()
val releaseKeyAlias = System.getenv("SCHOOLBRIDGE_UPLOAD_KEY_ALIAS")?.trim().orEmpty()
val releaseKeyPassword = System.getenv("SCHOOLBRIDGE_UPLOAD_KEY_PASSWORD")?.trim().orEmpty()
val hasReleaseSigning = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { it.isNotBlank() }

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
        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${normalizeBuildConfigString(schoolBridgeApiBaseUrl)}\""
        )
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
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
    kotlinOptions { jvmTarget = "11" }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        // MUST align with Kotlin 1.9.23+ for alpha Material 3
        kotlinCompilerExtensionVersion = "1.6.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {
    /* ---------- Compose core (BOM) ---------- */
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.geometry)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.foundation.layout)
    //implementation(libs.androidx.compose.testing)          // e.g. 2024.06.00
    androidTestImplementation(platform(libs.androidx.compose.bom))

    /* ---------- Compose UI toolkit ---------- */
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    /* ---------- Material 3 (expressive) ---------- */
    // Override the BOM with the newer alpha that ships FloatingToolbar
    val m3Alpha = "1.4.0-alpha15"
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material3:material3:$m3Alpha")
    implementation("androidx.compose.material3:material3-window-size-class:$m3Alpha")
    implementation("androidx.compose.material:material-icons-extended")   // Icons

    /* ---------- Navigation & accompanist ---------- */
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.27.0")

    /* ---------- Foundation / AndroidX ---------- */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    /* ---------- Networking ---------- */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    implementation("io.ktor:ktor-client-logging:2.3.11")
    implementation("io.ktor:ktor-client-okhttp:2.3.11")
    implementation("io.ktor:ktor-client-auth:2.3.11")
    implementation("io.ktor:ktor-client-resources:2.3.11")
    implementation("io.ktor:ktor-client-websockets:2.3.11")

    /* ---------- JSON / Serialization ---------- */
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    /* ---------- Data, Work, DI ---------- */
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    // ksp("com.google.dagger:hilt-android-compiler:2.51.1") // if you enable ksp plugin
    // ksp("androidx.hilt:hilt-compiler:1.2.0")

    /* ---------- Misc UI ---------- */
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.8")

    /* ---------- Testing ---------- */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation("androidx.compose.material:material")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

}
