import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val localProperties = Properties()
try {
    localProperties.load(FileInputStream(rootProject.file("key.properties")))
} catch (_ : Exception) {
    logger.warn("No Local Properties File Found!")
}

android {
    namespace = "com.softwareoverflow.maxigriphangboardtrainer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.softwareoverflow.maxigriphangboardtrainer"
        minSdk = 26
        targetSdk = 35
        versionCode = 7
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        buildConfigField("String[]", "DEV_DEVICES", "new String[] ${localProperties["testDeviceIds"]}")
    }

    buildTypes {
        debug {
            versionNameSuffix = ".debug"
            resValue("string", "app_version", "Version ${defaultConfig.versionName}${versionNameSuffix}")
            resValue("string", "ad_id_banner", "\"ca-app-pub-3940256099942544/6300978111\"")
            resValue("string", "ad_id_workout_start", "\"ca-app-pub-3940256099942544/1033173712\"")
            resValue("string", "ad_id_workout_end", "\"ca-app-pub-3940256099942544/1033173712\"")
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "app_version", "Version ${defaultConfig.versionName}")

            resValue("string", "ad_id_banner", "\"ca-app-pub-5961771507160254/3105386278\"")
            resValue("string", "ad_id_workout_start", "\"ca-app-pub-5961771507160254/9362730913\"")
            resValue("string", "ad_id_workout_end", "\"ca-app-pub-5961771507160254/3368168205\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    /*applicationVariants.all { variant ->
        variant.addJavaSourceFoldersToModel(
                new File(buildDir, "generated/ksp/${variant.name}/kotlin")
        )
    }*/
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.2.0"))
    implementation(platform("androidx.compose:compose-bom:2025.09.01"))

    implementation("org.jetbrains.kotlin:kotlin-compose-compiler-plugin:2.2.20")

    implementation("androidx.activity:activity-compose")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui-viewbinding")
    //implementation("androidx.compose.ui:ui-graphics-android:1.6.3")


    // Preview not working without these
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Compose Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.5")
    implementation("androidx.navigation:navigation-compose")
    implementation("io.github.raamcosta.compose-destinations:core:2.2.0")
    ksp("io.github.raamcosta.compose-destinations:ksp:2.2.0")


    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation("com.google.android.play:review:2.0.2")
    implementation("com.google.android.play:review-ktx:2.0.2")

    implementation("com.chargemap.compose:numberpicker:1.0.3")


    // Room
    implementation("androidx.room:room-common:2.8.3")
    implementation("androidx.room:room-ktx:2.8.3")
    implementation("androidx.room:room-runtime:2.8.3")
    ksp("androidx.room:room-compiler:2.8.3")

    // Play Services - using BoM
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.android.gms:play-services-ads:24.7.0")

    // Monetization
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.google.android.ump:user-messaging-platform:3.2.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
}