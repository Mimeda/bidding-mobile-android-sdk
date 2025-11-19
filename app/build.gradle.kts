plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.mimeda.sdk"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configuration values - gradle.properties'ten override edilebilir
        val apiBaseUrl = project.findProperty("MIMEDA_API_BASE_URL") as String? ?: "https://api.mimeda.com"
        val connectTimeout = (project.findProperty("MIMEDA_CONNECT_TIMEOUT") as String?)?.toLongOrNull() ?: 10L
        val readTimeout = (project.findProperty("MIMEDA_READ_TIMEOUT") as String?)?.toLongOrNull() ?: 30L
        val writeTimeout = (project.findProperty("MIMEDA_WRITE_TIMEOUT") as String?)?.toLongOrNull() ?: 30L
        val enableDebugLogging = (project.findProperty("MIMEDA_DEBUG_LOGGING") as String?)?.toBoolean() ?: false

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("long", "CONNECT_TIMEOUT_SECONDS", "${connectTimeout}L")
        buildConfigField("long", "READ_TIMEOUT_SECONDS", "${readTimeout}L")
        buildConfigField("long", "WRITE_TIMEOUT_SECONDS", "${writeTimeout}L")
        buildConfigField("boolean", "DEBUG_LOGGING", if (enableDebugLogging) "true" else "false")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    
    // HTTP Client
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    
    // JSON Parsing
    implementation(libs.gson)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}