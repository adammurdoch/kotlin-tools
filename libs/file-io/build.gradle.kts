import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
    id("net.rubygrapefruit.bootstrap.release")
}

group = Versions.libs.group

library {
    jvm {
        targetJavaVersion = Versions.plugins.java
        module.name = "net.rubygrapefruit.file_io"
    }
    nativeDesktop()
    common {
        api(project(":stream-io"))
        implementation(Versions.libs.coordinates("basics"))
    }
    test {
        implementation(Versions.test.coordinates)
        implementation(project(":file-fixtures"))
    }
}

release {
    description = "A Kotlin multiplatform library for accessing the file system."
}
