plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    signing
    jacoco
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
        val maxRetries = (project.findProperty("MIMEDA_MAX_RETRIES") as String?)?.toIntOrNull() ?: 3
        val retryBaseDelayMs = (project.findProperty("MIMEDA_RETRY_BASE_DELAY_MS") as String?)?.toLongOrNull() ?: 1000L

        // BuildConfig field'ları (URL'ler productFlavors'da tanımlı)
        buildConfigField("String", "SDK_VERSION", "\"$sdkVersion\"")
        buildConfigField("long", "CONNECT_TIMEOUT_SECONDS", "${connectTimeout}L")
        buildConfigField("long", "READ_TIMEOUT_SECONDS", "${readTimeout}L")
        buildConfigField("long", "WRITE_TIMEOUT_SECONDS", "${writeTimeout}L")
        buildConfigField("boolean", "DEBUG_LOGGING", if (enableDebugLogging) "true" else "false")
        buildConfigField("int", "MAX_RETRIES", "$maxRetries")
        buildConfigField("long", "RETRY_BASE_DELAY_MS", "${retryBaseDelayMs}L")
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
    
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    
    publishing {
        singleVariant("productionRelease") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testStagingDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )
    
    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/stagingDebug") {
        exclude(fileFilter)
    }
    
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(buildDir) {
        include("jacoco/testStagingDebugUnitTest.exec")
    })
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
    testImplementation("org.mockito:mockito-core:5.0.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.3.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

// ============================================
// Maven Publishing Configuration
// ============================================

val sdkVersion = project.findProperty("MIMEDA_SDK_VERSION") as String? ?: "1.0.0"
val groupId = "tr.com.mimeda"
val artifactId = "bidding-mobile-android-sdk"

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                // Production release variant'ını kullan
                from(components["productionRelease"])
                
                groupId = groupId
                artifactId = artifactId
                version = sdkVersion
                
                pom {
                    name.set("Mimeda Android SDK")
                    description.set("Android SDK for Mimeda bidding platform")
                    url.set("https://github.com/mimeda")
                    
                    developers {
                        developer {
                            name.set("Mimeda Team")
                            organization.set("Mimeda")
                            organizationUrl.set("https://mimeda.com.tr")
                        }
                    }
                }
            }
        }
        
        repositories {
            maven {
                val isSnapshot = sdkVersion.endsWith("-SNAPSHOT") || sdkVersion.contains("-beta")
                val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                
                url = if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl
                
                credentials {
                    username = project.findProperty("OSSRH_USERNAME") as String? ?: ""
                    password = project.findProperty("OSSRH_PASSWORD") as String? ?: ""
                }
            }
        }
    }
    
    // Signing - useGpgCmd() ile sistem GPG'sini kullanır
    signing {
        // CI ortamında GPG key import edilmiş olmalı
        useGpgCmd()
        sign(publishing.publications["release"])
    }
}