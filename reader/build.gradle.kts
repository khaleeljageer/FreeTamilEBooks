plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    kotlin("plugin.parcelize")
}

android {
    namespace = "com.jskhaleel.reader"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
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

    packaging {
        resources.excludes.add("META-INF/*")
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
            res.srcDirs("src/main/res")
            assets.srcDirs("src/main/assets")
        }
    }
}

//noinspection UseTomlInstead
dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-core-android")
    implementation("androidx.compose.material:material-icons-extended-android")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.browser:browser:1.8.0")

    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")

    implementation("org.readium.kotlin-toolkit:readium-shared:3.0.0-beta.1")
    implementation("org.readium.kotlin-toolkit:readium-streamer:3.0.0-beta.1")
    implementation("org.readium.kotlin-toolkit:readium-opds:3.0.0-beta.1")
    implementation("org.readium.kotlin-toolkit:readium-lcp:3.0.0-beta.1")
    implementation("org.readium.kotlin-toolkit:readium-navigator:3.0.0-beta.1")
    implementation("org.readium.kotlin-toolkit:readium-navigator-media2:3.0.0-beta.1")
    implementation("org.readium.kotlin-toolkit:readium-navigator-media-tts:3.0.0-beta.1")
    implementation("org.readium.kotlin-toolkit:readium-navigator-media-audio:3.0.0-beta.1")

    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    // https://mvnrepository.com/artifact/joda-time/joda-time
    implementation("joda-time:joda-time:2.12.7")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.3")

    implementation("androidx.media2:media2-session:1.3.0")
    implementation("androidx.media2:media2-player:1.3.0")
    implementation("androidx.media3:media3-common:1.4.0")
    implementation("androidx.media3:media3-session:1.4.0")
    implementation("androidx.media3:media3-exoplayer:1.4.0")

    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
}