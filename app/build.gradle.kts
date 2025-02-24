plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.gai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gai"
        minSdk = 24
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose) // Already added
    implementation(platform(libs.androidx.compose.bom)) // Already added
    implementation(libs.androidx.ui) // Already added
    implementation(libs.androidx.ui.graphics) // Already added
    implementation(libs.androidx.ui.tooling.preview) // Already added
    implementation(libs.androidx.material3) // Already added

    // Add ActivityResultContracts dependency
    implementation("androidx.activity:activity-ktx:1.10.0") // Needed for ActivityResult API

    // Optional but recommended for using Composes image bitmap support
    implementation("androidx.compose.ui:ui-tooling:1.10.0")

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}