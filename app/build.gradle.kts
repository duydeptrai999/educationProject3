plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.duyth10.intentservice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.duyth10.intentservice"
        minSdk = 22
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val camerax_version = "1.0.2" // Stable version compatible with Android 7
    implementation ("androidx.camera:camera-core:$camerax_version")
    implementation ("androidx.camera:camera-camera2:$camerax_version")
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")
    implementation ("androidx.camera:camera-view:1.0.0-alpha30")
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha30")

    // ML Kit Barcode Scanner
    implementation ("com.google.mlkit:barcode-scanning:16.0.2")

    // Kotlin stdlib and support libraries
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.8.21") // Ensure you have Kotlin set up
    implementation ("androidx.core:core-ktx:1.10.1")
    implementation ("androidx.appcompat:appcompat:1.4.2")
    // Lifecycle and ViewModel
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // DataBinding and ViewBinding
    implementation ("androidx.databinding:databinding-runtime:7.0.0")

    // For ListenableFuture used in CameraX
    implementation ("com.google.guava:guava:31.1-android")

    // Legacy support for Android 7 compatibility
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")

    implementation ("com.google.mlkit:barcode-scanning:17.0.0")

}