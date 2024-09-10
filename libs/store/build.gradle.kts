import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm {
        targetJavaVersion = Versions.plugins.java
        module.name = "net.rubygrapefruit.store"
    }
    nativeDesktop()
    common {
        api(project(":file-io"))
        api(Versions.serialization.coordinates)
        implementation(project(":stream-io"))
        implementation(Versions.serialization.json.coordinates)
    }
    test {
        implementation(Versions.test.coordinates)
        implementation(project(":file-fixtures"))
    }
}
