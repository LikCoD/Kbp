pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        maven { url = uri("https://jitpack.io") }
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        maven { url = uri("https://jitpack.io") }
    }
}

include(":app")
rootProject.name = "Studyum"