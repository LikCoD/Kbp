plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("kotlinx-serialization")
    id("kotlin-android")
}

android {
    compileSdkVersion(31)

    defaultConfig {
        applicationId = "com.ldc.kbp"
        minSdkVersion(24)
        targetSdkVersion(31)
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.jakewharton.threetenabp:threetenabp:1.0.3")
    implementation("joda-time:joda-time:2.10.12")
    implementation("com.github.OKatrych:RightSheetBehavior:1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.2.2")
    implementation("com.yarolegovich:discrete-scrollview:1.5.1")
    implementation("com.github.OzcanAlasalvar:DatePicker:1.0.4")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.jaredrummler:twodscrollview:1.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    runtimeOnly("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    runtimeOnly("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

    implementation("androidx.core:core-ktx:1.7.0-alpha02")
    implementation("androidx.appcompat:appcompat:1.4.0-alpha03")
    implementation("com.google.android.material:material:1.5.0-alpha03")
    implementation("com.itextpdf:io:7.1.16")
    implementation("com.itextpdf:kernel:7.1.16")
    implementation("com.itextpdf:layout:7.1.16")
    implementation("com.tom_roush:pdfbox-android:1.8.10.1")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("com.jsibbold:zoomage:1.3.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}