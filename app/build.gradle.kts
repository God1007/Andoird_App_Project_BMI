import java.io.File
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.bmicalculation"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bmicalculation"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "LLM_API_KEY", "\"\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

fun detectDrawableFormat(file: File): String? {
    val bytes = file.readBytes()
    return when {
        bytes.size >= 12 &&
            bytes[0] == 0x52.toByte() && bytes[1] == 0x49.toByte() && // RIFF
            bytes[2] == 0x46.toByte() && bytes[3] == 0x46.toByte() &&
            bytes[8] == 0x57.toByte() && bytes[9] == 0x45.toByte() && // WEBP
            bytes[10] == 0x42.toByte() && bytes[11] == 0x50.toByte() -> "webp"

        bytes.size >= 4 &&
            bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() && // .PNG
            bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte() -> "png"

        bytes.size >= 3 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() -> "jpeg"
        else -> null
    }
}

val validateDrawableExtensions by tasks.registering {
    group = "verification"
    description = "Validates that drawable file extensions match their actual formats."

    doLast {
        val drawableDir = file("src/main/res/drawable")
        if (!drawableDir.exists()) return@doLast

        drawableDir.listFiles()?.forEach { drawable ->
            val ext = drawable.extension.lowercase(Locale.US)
            if (ext.isEmpty()) return@forEach

            val detected = detectDrawableFormat(drawable) ?: return@forEach
            val expected = when (ext) {
                "jpg", "jpeg" -> "jpeg"
                "png" -> "png"
                "webp" -> "webp"
                else -> return@forEach
            }

            if (detected != expected) {
                error("Drawable ${drawable.name} has extension .$ext but appears to be $detected")
            }
        }
    }
}

tasks.named("preBuild") {
    dependsOn(validateDrawableExtensions)
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
