import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
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
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            //Adaptive
            api(libs.androidx.material3.adaptive.navigation.suite)
//            api(libs.androidx.adaptive)
//            api(libs.androidx.adaptive.layout)
//            api(libs.androidx.adaptive.navigation)
            implementation(libs.androidx.activity.compose)
            api(libs.haze)

            //SplashScreen API
            api(libs.androidx.core.splashscreen)
        }
        androidUnitTest.dependencies {
            implementation(libs.bundles.unit.test)
//            androidTestImplementation(libs.bundles.android.unit.test)
        }
        androidInstrumentedTest.dependencies {
//            testImplementation(libs.bundles.unit.test)
            implementation(libs.bundles.android.unit.test)
        }
        commonMain.dependencies {
            //put your multiplatform dependencies here
            api(projects.core.core)

            implementation(compose.material3)
            implementation(compose.components.resources)

            api(libs.bundles.compose.multiplatform)
            //DroidCore
            api(libs.droidcore)
            // Coil
            implementation(libs.bundles.coil)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "id.neotica.ui"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

//dependencies {
//
////    api("com.valentinilk.shimmer:compose-shimmer:1.3.1")
//
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//}