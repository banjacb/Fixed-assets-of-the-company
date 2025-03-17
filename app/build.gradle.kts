plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.projektnizadatak"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projektnizadatak"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
   implementation(libs.room.runtime)
    implementation(libs.preference)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
   annotationProcessor(libs.room.compiler)
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("androidx.room:room-runtime:2.2.5")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
        //  annotationProcessor ("androidx.room:room-compiler:2.2.5")

    //implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
   //implementation ("com.google.zxing:core:3.4.0")
   // implementation("com.google.zxing:core:3.5.3")
  //  implementation("com.journeyapps:barcodescanner:4.3.0")







}


