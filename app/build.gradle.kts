import com.android.build.api.variant.BuildConfigField

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.taitsmith.weatherman"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.taitsmith.weatherman"
        minSdk = 26
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    kotlin {
            jvmToolchain(17)
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("com.google.android.material:material:1.9.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    implementation("com.google.dagger:hilt-android:2.47")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.6")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.6")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    kapt("com.google.dagger:hilt-compiler:2.47")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.47")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    kaptTest("com.google.dagger:hilt-compiler:2.47")
}

kapt {
    correctErrorTypes = true
}