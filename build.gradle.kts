// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by rootProject.extra { "1.5.21" }

    repositories {
        mavenCentral()
        google()
        maven("https://plugins.gradle.org/m2/")
        maven("https://storage.googleapis.com/r8-releases/raw")
    }

    dependencies {
        // Fix https://issuetracker.google.com/issues/192023718
        // TODO: Remove once we update to AGP 7.0
        classpath("com.android.tools:r8:2.2.77")

        classpath("com.android.tools.build:gradle:7.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:8.9.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.37")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
        // Required for Xposed API library
        jcenter()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
