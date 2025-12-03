import java.io.File

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    //id("com.google.protobuf")
    alias(libs.plugins.google.protobuf)
}

android {
    namespace = "com.scto.codelikebastimove.core.datastore.proto"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

protobuf {
    protoc {
        // Der Standard-Pfad für protoc in Termux
        val termuxProtocPath = "/data/data/com.tom.rv2ide/files/usr/bin/protoc"
        val termuxProtocFile = File(termuxProtocPath)

        if (termuxProtocFile.exists()) {
            // Nutze die lokale Installation auf dem Handy
            path = termuxProtocPath
        } else {
            // Fallback für deinen PC/Mac (lädt das Artefakt herunter)
            artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
        }
    }

    // WICHTIG: Die generierten Klassen (Lite vs Java)
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                
                /*
                create("kotlin") {
                    option("lite")
                }
                */
                
            }
        }
    }
}

dependencies {
    api(libs.protobuf.kotlin)
}