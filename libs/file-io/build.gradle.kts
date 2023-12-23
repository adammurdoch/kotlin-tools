import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm {
        targetJavaVersion = Versions.plugins.java
        module.name = "net.rubygrapefruit.file_io"
    }
    nativeDesktop()
    common {
        api(project(":stream-io"))
    }
    test {
        implementation(kotlin("test"))
        implementation(project(":file-fixtures"))
    }
}
