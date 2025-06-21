rootProject.name = "Neoverse"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":app")
include(":neoversewear")
include(":core")
include(":core:database")
include(":core:domain")
include(":core:network")
include(":core:ui")
include(":core:core")
include(":core:retrofit")
include(":core:camera")
include(":feature")
include(":feature:world")
include(":feature:raven")
include(":feature:tavern")
include(":feature:alexandria")
include(":feature:profile")
include(":feature:auth")
include(":feature:chat")
include(":feature:home")
include(":feature:friend")
include(":alexandriaApp")
include(":core:ui-shared")
include(":core:routes")
include(":core:firebase")
include(":core:ktor")
