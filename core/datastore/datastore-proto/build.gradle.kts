import java.io.File

plugins {
    id("clbm.android.library")
    alias(libs.plugins.google.protobuf)
}

android {
    namespace = "com.scto.codelikebastimove.core.datastore.proto"
}

protobuf {
    protoc {
        val termuxProtocPath = "/data/data/com.tom.rv2ide/files/usr/bin/protoc"
        val termuxProtocFile = File(termuxProtocPath)

        if (termuxProtocFile.exists()) {
            path = termuxProtocPath
        } else {
            artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    api(libs.protobuf.kotlin)
}
