import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm {
        targetJavaVersion = Versions.plugins.java
        module.name = "net.rubygrapefruit.file_fixtures"
    }
    nativeDesktop()
    common {
        api(kotlin("test"))
        api(project(":file-io"))
    }
}
