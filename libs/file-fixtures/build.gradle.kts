import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = Versions.libs.group

library {
    jvm {
        targetJavaVersion = Versions.plugins.java
        module.name = "net.rubygrapefruit.file_fixtures"
        dependencies {
            api(Versions.test.junit.coordinates)
        }
    }
    nativeDesktop()
    common {
        api(Versions.test.coordinates)
        api(project(":file-io"))
    }
}
