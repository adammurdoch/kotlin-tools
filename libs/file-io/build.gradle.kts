import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
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
