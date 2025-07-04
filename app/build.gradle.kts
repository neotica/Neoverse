plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "id.neotica.neoverse"
    compileSdk = 36

    defaultConfig {
        applicationId = "id.neotica.neoverse"
        minSdk = 26
        targetSdk = 36
        versionCode = 66
        versionName = "1.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
//            isMinifyEnabled = true
//            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.network)
    implementation(projects.core.ui)
    implementation(projects.core.routes)
    implementation(projects.core.core)
    implementation(projects.core.camera)
    implementation(projects.feature.world)
    implementation(projects.feature.auth)
    implementation(projects.feature.profile)
    implementation(projects.feature.home)
    implementation(projects.feature.chat)
    implementation(projects.feature.friend)

    // Testing
    testImplementation(libs.bundles.unit.test)
    androidTestImplementation(libs.bundles.android.unit.test)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(libs.androidx.ui.test.junit4)
}