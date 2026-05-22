@file:Suppress("UnstableApiUsage")
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Audily"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")

include(":core:designsystem")
include(":core:navigation")
include(":core:common")
include(":core:network")
include(":core:model")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":core:playback")
include(":core:datastore")
include(":core:database")
include(":core:mediastore")
include(":core:palette")

include(":feature:home:api")
include(":feature:home:impl")

include(":feature:focus:api")

include(":feature:settings:api")

include(":feature:songs:api")
include(":feature:songs:impl")

include(":feature:albums:api")

include(":feature:playlists:api")
include(":feature:playlists:impl")

include(":feature:nowplaying")

include(":feature:search:api")
include(":feature:search:impl")

include(":feature:favorites:api")
include(":feature:favorites:impl")

include(":feature:edittag:api")
include(":feature:edittag:impl")


