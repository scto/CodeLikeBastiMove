plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.logger"
    
    buildFeatures {
        buildConfig = true
    }
    
    defaultConfig {
        buildConfigField("boolean", "LOGGING_DEFAULT_ENABLED", "true")
    }
    
    buildTypes {
        release {
            buildConfigField("boolean", "LOGGING_DEFAULT_ENABLED", "false")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
