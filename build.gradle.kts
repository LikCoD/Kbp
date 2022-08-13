buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.13")
        classpath("com.android.tools.build:gradle:7.4.0-alpha09")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.android.application").version("7.4.0-alpha09").apply(false)
    id("com.android.library").version("7.4.0-alpha09").apply(false)

    kotlin("plugin.serialization").version("1.7.0").apply(false)
    kotlin("android").version("1.7.0").apply(false)
}
