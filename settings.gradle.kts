plugins { 
    alias(libs.plugins.android.application) 
    id ("com.google.gms.google-services")  // Apply the Google services plugin 
} 
 
android { 
    namespace = "com.locationtracker" 
    compileSdk = 35 
 
    defaultConfig { 
        applicationId = "com.example.locationtracker" 
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
} 
 
dependencies { 
 
    implementation(libs.appcompat) 
    implementation(libs.material) 
    implementation(libs.activity) 
    implementation(libs.constraintlayout) 
    implementation(libs.play.services.location) 
    testImplementation(libs.junit) 
    androidTestImplementation(libs.ext.junit) 
    androidTestImplementation(libs.espresso.core) 
    implementation ("com.google.firebase:firebase-auth:23.2.0") 
    implementation ("com.google.firebase:firebase-database:21.0.0") 
    implementation ("com.google.firebase:firebase-messaging:24.1.1") 
    implementation ("com.google.android.gms:play-services-maps:19.2.0") 
    implementation ("com.google.android.gms:play-services-location:21.3.0") 
    implementation ("com.google.android.gms:play-services-maps:19.2.0") 
    implementation ("com.google.android.gms:play-services-location:21.3.0") 
} 
