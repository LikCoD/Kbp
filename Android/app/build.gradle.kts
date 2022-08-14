plugins {
    id("com.android.application")
    id("com.google.gms.google-services")

    kotlin("android")
    kotlin("plugin.serialization")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "likco.studyum"
    compileSdk = 33

    defaultConfig {
        applicationId = "likco.studyum"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

dependencies {
    //image from url
    implementation("io.coil-kt:coil-compose:2.0.0-rc01")

    //http requests
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-android:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-kotlinx-serialization:2.3.1")

    //pdf
    implementation("com.itextpdf:io:7.1.16")
    implementation("com.itextpdf:kernel:7.1.16")
    implementation("com.itextpdf:layout:7.1.16")

    //compose
    val composeVersion = "1.3.0-alpha02"
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:29.0.3"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    //json (de)serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")

    //androidx
    implementation("androidx.core:core-ktx:1.9.0-alpha05")
    implementation("androidx.activity:activity-compose:1.6.0-alpha05")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")

    //tests
    testImplementation("org.junit:junit-bom:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}