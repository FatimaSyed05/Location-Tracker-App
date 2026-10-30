  buildscript { 
        repositories { 
            google()  // Firebase plugin ko load karne ke liye 
            mavenCentral() 
        } 
 
        dependencies { 
            // Add this line for Firebase Google Services plugin 
            classpath ("com.google.gms:google-services:4.4.2") 
        } 
    } 
plugins { 
    alias(libs.plugins.android.application) apply false 
}