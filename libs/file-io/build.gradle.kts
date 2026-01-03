plugins {
    id("net.rubygrapefruit.kmp.base-lib")
    id("net.rubygrapefruit.bootstrap.release")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = 11
        module.name = "net.rubygrapefruit.file_io"
    }
    nativeDesktop()
    common {
        api(project(":stream-io"))
        implementation(versions.libs.coordinates("basics"))
    }
    test {
        implementation(versions.test.coordinates)
        implementation(project(":file-fixtures"))
    }
}

component {
    description = "A Kotlin multiplatform library for accessing the file system."
}
