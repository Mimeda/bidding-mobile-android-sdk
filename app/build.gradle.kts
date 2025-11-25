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

    flavorDimensions += "environment"

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configuration values - gradle.properties'ten override edilebilir
        val sdkVersion = project.findProperty("MIMEDA_SDK_VERSION") as String? ?: "1.0.0"
        val connectTimeout = (project.findProperty("MIMEDA_CONNECT_TIMEOUT") as String?)?.toLongOrNull() ?: 10L
        val readTimeout = (project.findProperty("MIMEDA_READ_TIMEOUT") as String?)?.toLongOrNull() ?: 30L
        val writeTimeout = (project.findProperty("MIMEDA_WRITE_TIMEOUT") as String?)?.toLongOrNull() ?: 30L
        val enableDebugLogging = (project.findProperty("MIMEDA_DEBUG_LOGGING") as String?)?.toBoolean() ?: false

        // BuildConfig field'ları (URL'ler productFlavors'da tanımlı)
        buildConfigField("String", "SDK_VERSION", "\"$sdkVersion\"")
        buildConfigField("long", "CONNECT_TIMEOUT_SECONDS", "${connectTimeout}L")
        buildConfigField("long", "READ_TIMEOUT_SECONDS", "${readTimeout}L")
        buildConfigField("long", "WRITE_TIMEOUT_SECONDS", "${writeTimeout}L")
        buildConfigField("boolean", "DEBUG_LOGGING", if (enableDebugLogging) "true" else "false")
    }

    productFlavors {
        create("production") {
            dimension = "environment"
            // Her build'de her iki environment URL'i de mevcut olmalı (runtime seçimi için)
            buildConfigField("String", "PRODUCTION_EVENT_BASE_URL", "\"https://event.mlink.com.tr\"")
            buildConfigField("String", "PRODUCTION_PERFORMANCE_BASE_URL", "\"https://performance.mlink.com.tr\"")
            buildConfigField("String", "STAGING_EVENT_BASE_URL", "\"https://bidding-eventcollector-stage.azurewebsites.net\"")
            buildConfigField("String", "STAGING_PERFORMANCE_BASE_URL", "\"https://bidding-prfmnccollector-stage.azurewebsites.net\"")
        }

        create("staging") {
            dimension = "environment"
            // Her build'de her iki environment URL'i de mevcut olmalı (runtime seçimi için)
            buildConfigField("String", "PRODUCTION_EVENT_BASE_URL", "\"https://event.mlink.com.tr\"")
            buildConfigField("String", "PRODUCTION_PERFORMANCE_BASE_URL", "\"https://performance.mlink.com.tr\"")
            buildConfigField("String", "STAGING_EVENT_BASE_URL", "\"https://bidding-eventcollector-stage.azurewebsites.net\"")
            buildConfigField("String", "STAGING_PERFORMANCE_BASE_URL", "\"https://bidding-prfmnccollector-stage.azurewebsites.net\"")
        }
    }

    buildTypes {
        debug {
            // Debug build type için BuildConfig field'ları defaultConfig'ten devralınır
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Release build type için BuildConfig field'ları defaultConfig'ten devralınır
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
    implementation("androidx.core:core-ktx:1.17.0")
    
    // HTTP Client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // JSON Parsing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}