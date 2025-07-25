import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Hadall"
            isStatic = true
        }
    }

    sourceSets {
//        val desktopMain by getting

        androidMain.dependencies {
            implementation(projects.core.network)
        }
        androidUnitTest.dependencies {
            implementation(libs.bundles.unit.test)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.bundles.android.unit.test)
        }
        iosMain.dependencies {

        }
        commonMain.dependencies {
            implementation(projects.core.core)
            implementation(projects.core.database)
            implementation(projects.core.domain)
            implementation(projects.core.uiShared)
            implementation(projects.core.routes)
            implementation(projects.core.ktor)

            implementation(projects.feature.alexandria)
            implementation(projects.feature.profile)
            implementation(projects.feature.auth)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
//        desktopMain.dependencies {
////            implementation(compose.desktop.currentOs)
////            implementation(libs.kotlinx.coroutinesSwing)
//        }
    }
}

android {
    namespace = "id.neotica.hadall"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "id.neotica.hadall"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1"

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
            isShrinkResources = true
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
}