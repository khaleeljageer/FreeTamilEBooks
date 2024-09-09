plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    kotlin("plugin.parcelize")
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.epubreader.android"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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

    packaging {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
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
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.fragment:fragment-ktx:1.8.3")
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")

    //==================== Compose ====================//
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.material:material:1.7.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0")
    implementation("com.google.android.material:compose-theme-adapter:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    //==================== Readium ====================//
    implementation("org.readium.kotlin-toolkit:readium-shared:2.3.0")
    implementation("org.readium.kotlin-toolkit:readium-streamer:2.3.0")
    implementation("org.readium.kotlin-toolkit:readium-navigator:2.3.0")
    implementation("org.readium.kotlin-toolkit:readium-opds:2.3.0")
    implementation("org.readium.kotlin-toolkit:readium-lcp:2.3.0")
    implementation("org.readium.kotlin-toolkit:readium-navigator-media2:2.3.0")
    implementation("org.readium.kotlin-toolkit:readium-shared:2.3.0")

    //==================== Database ====================//
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("joda-time:joda-time:2.12.7")

    //==================== Logging ====================//
    implementation("com.jakewharton.timber:timber:5.0.1")

    //==================== Dependency Injection ====================
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}