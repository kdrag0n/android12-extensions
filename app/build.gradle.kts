plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    compileSdkVersion = "android-S"
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "dev.kdrag0n.android12ext"
        minSdkVersion(30)
        targetSdkVersion(30)
        versionCode = 50101
        versionName = "5.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        // Module info for kotlin-reflect - leaks info
        exclude("/META-INF/*.kotlin_module")
        exclude("/META-INF/*.version")
        // Builtin info for kotlin-reflect
        exclude("/kotlin/**")
    }
}

kapt {
    correctErrorTypes = true

    javacOptions {
        // These options are normally set automatically via the Hilt Gradle plugin, but we
        // set them manually to workaround a bug in the Kotlin 1.5.20
        option("-Adagger.fastInit=ENABLED")
        option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.fragment:fragment:1.3.5")
    implementation("com.google.android.material:material:1.4.0-rc01")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha01")
    implementation("com.google.dagger:hilt-android:2.37")
    kapt("com.google.dagger:hilt-compiler:2.37")

    implementation("de.maxr1998:modernandroidpreferences:2.1.0")
    implementation("com.microsoft.design:fluent-system-icons:1.1.134")
    implementation("dev.chrisbanes.insetter:insetter:0.6.0")
    implementation("com.crossbowffs.remotepreferences:remotepreferences:0.8")
    implementation("com.mikepenz:aboutlibraries:8.9.0")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:2.0")
    implementation("com.jaredrummler:colorpicker:1.1.0")
    implementation("com.github.topjohnwu.libsu:core:3.1.2")
    implementation("com.github.Zhuinden:fragmentviewbindingdelegate-kt:1.0.0")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
}
