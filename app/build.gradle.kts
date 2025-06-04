plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
}

android {
    namespace = "io.branch.branchlinksimulator"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.branch.branchlinksimulator"
        minSdk = 26
        targetSdk = 35

        // Access the project property passed from the CI/CD pipeline
        // Use findProperty() for safe access, returns null if property is not found
        val versionNameFromCi = project.findProperty("versionNameFromCi") as String?

        // Define a fallback version for local development or if CI property is missing
        val defaultVersionName = "0.0.0-DEV"
        val actualVersionName = versionNameFromCi ?: defaultVersionName

        versionName = actualVersionName

        // Logic to derive versionCode from actualVersionName
        versionCode = actualVersionName.getVersionCode()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

repositories {
    flatDir {
        dirs("libs") // This tells Gradle to look for AARs in the 'libs' directory
    }
    google()
    mavenCentral()
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation(files("libs/branch-sdk-debug.aar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //implementation("io.branch.sdk.android:library:5.18.1")

    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.android.installreferrer:installreferrer:2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("androidx.browser:browser:1.8.0")
}

// Extension function to calculate versionCode from a version string
fun String.getVersionCode(): Int {
    // Remove any pre-release or build metadata (e.g., "-DEV", "+build123")
    val cleanedVersion = this.split("-", "+")[0]

    val parts = cleanedVersion.split(".").map { it.toIntOrNull() ?: 0 } // Convert to Int, default to 0 if not a number

    // Ensure we have at least major, minor, patch
    val major = parts.getOrElse(0) { 0 }
    val minor = parts.getOrElse(1) { 0 }
    val patch = parts.getOrElse(2) { 0 }

    // You can adjust these multipliers based on your versioning scheme and expected ranges
    // Example: MAJOR * 1000000 + MINOR * 1000 + PATCH
    // This allows for MINOR and PATCH up to 999
    return major * 1_000_000 + minor * 1_000 + patch
}