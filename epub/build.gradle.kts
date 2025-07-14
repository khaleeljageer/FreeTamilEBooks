import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    kotlin("plugin.parcelize")
}

android {
    namespace = "com.jskaleel.epub"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("17")
            freeCompilerArgs = listOf(
                "-XXLanguage:+PropertyParamAnnotationDefaultTargetMode"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
            res.srcDirs("src/main/res")
            assets.srcDirs("src/main/assets")
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Readium Kotlin Toolkit
    implementation(libs.bundles.readium.core)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.joda.time)
    implementation(libs.timber)
    implementation(libs.bundles.media3)
    implementation(libs.androidx.browser)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}