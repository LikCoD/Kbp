plugins {
    id("com.android.application")

    kotlin("android")
    kotlin("android.extensions")
    kotlin("plugin.serialization")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.ldc.kbp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.ldc.kbp"
        minSdk = 24
        targetSdk = 33
        versionCode = 20
        versionName = "2.0"
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
        kotlinCompilerVersion = "1.7.0"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
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
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:29.0.3"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    debugImplementation("androidx.compose.ui:ui-tooling:1.3.0-alpha02")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.0-alpha02")
    implementation("androidx.compose.ui:ui:1.3.0-alpha02")
    implementation("androidx.compose.material:material:1.3.0-alpha02")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.0-alpha02")
    implementation("androidx.compose.runtime:runtime-livedata:1.3.0-alpha02")
    implementation("androidx.compose.material3:material3:1.0.0-alpha15")
    implementation("androidx.compose.material3:material3-window-size-class:1.0.0-alpha15")

    implementation("androidx.navigation:navigation-compose:2.5.1")
    implementation("io.coil-kt:coil-compose:2.0.0-rc01") //image from url

    //http requests
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-android:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-kotlinx-serialization:2.3.1")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.0.3")
    implementation("joda-time:joda-time:2.10.12")
    implementation("com.github.OKatrych:RightSheetBehavior:1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.0")

    implementation("com.yarolegovich:discrete-scrollview:1.5.1")
    implementation("com.github.OzcanAlasalvar:DatePicker:1.0.4")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.jaredrummler:twodscrollview:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
    implementation("androidx.core:core-ktx:1.9.0-alpha05")
    implementation("androidx.appcompat:appcompat:1.6.0-alpha05")
    implementation("com.itextpdf:io:7.1.16")
    implementation("com.itextpdf:kernel:7.1.16")
    implementation("com.itextpdf:layout:7.1.16")
    implementation("com.tom_roush:pdfbox-android:1.8.10.1")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("com.jsibbold:zoomage:1.3.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    testImplementation("org.junit:junit-bom:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}