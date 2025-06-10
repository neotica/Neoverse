plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "id.neotica.routes"
        compileSdk = 35
        minSdk = 21

    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "networkKit"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.core)
            api(projects.core.domain)
            api(libs.bundles.ktor)
        }
        androidMain.dependencies {
            implementation(projects.core.retrofit)
            implementation(libs.ktor.client.android)
            //Firebase
            api(project.dependencies.platform(libs.firebase.bom))
            api(libs.firebase.analytics)
            api(libs.firebase.auth)
            api(libs.firebase.database)
            api(libs.firebase.storage)
            api(libs.firebase.messaging)
            api(libs.firebase.firestore.ktx)
            api(libs.firebase.appcheck.playintegrity)
            api(libs.firebase.appcheck.debug)

        }
        androidUnitTest.dependencies {
            implementation(libs.bundles.unit.test)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.bundles.android.unit.test)
        }
        iosMain.dependencies { api(libs.ktor.client.darwin) }
    }
}