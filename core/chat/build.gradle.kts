plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "id.neotica.chat.client"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
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
    implementation(projects.core.core)
    implementation(projects.core.domain)
    implementation(libs.bundles.ktor)
    implementation(libs.ktor.client.websockets)
    implementation(libs.bundles.koin)
    implementation(libs.coroutines.core)

    testImplementation(libs.bundles.unit.test)
}
