pluginManagement {
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
        // Mapas (IT-7). Las versiones 11.x se descargan sin token secreto; si alguna lo pide,
        // se pone MAPBOX_DOWNLOADS_TOKEN en ~/.gradle/gradle.properties (nunca en el repositorio).
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            val downloadsToken = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").orNull
            if (!downloadsToken.isNullOrBlank()) {
                authentication { create<BasicAuthentication>("basic") }
                credentials {
                    username = "mapbox"
                    password = downloadsToken
                }
            }
            content { includeGroupByRegex("com\\.mapbox.*") }
        }
    }
}

rootProject.name = "apptours"
include(":app")
