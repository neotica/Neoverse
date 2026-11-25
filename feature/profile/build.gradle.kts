import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.moko.resources)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "profileKit"
            isStatic = true
        }
    }

    sourceSets {
        androidUnitTest.dependencies {
            implementation(libs.bundles.unit.test)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.bundles.android.unit.test)
        }
        androidMain.dependencies {
            implementation(projects.core.core)
        }
        commonMain.dependencies {
            api(projects.core.uiShared)
//            implementation(projects.core.database)
            implementation(projects.core.domain)
            implementation(projects.core.ktor)
            implementation(projects.core.routes)
            implementation(projects.feature.auth)
        }
    }
}

android {
    namespace = "id.neotica.profile"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}