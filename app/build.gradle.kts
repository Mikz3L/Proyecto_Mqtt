plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.proyecto_mqtt"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proyecto_mqtt"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // AndroidX libraries
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // MQTT Client library
    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation ("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1") // Esta es la librería específica para Android

    // Unit testing
    testImplementation("junit:junit:4.13.2")

    // Android test libraries
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
