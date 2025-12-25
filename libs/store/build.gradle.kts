plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = versions.plugins.java
        module.name = "net.rubygrapefruit.store"
    }
    nativeDesktop()
    common {
        api(project(":file-io"))
        api(versions.serialization.coordinates)
        implementation(project(":stream-io"))
        implementation(versions.serialization.json.coordinates)
    }
    test {
        implementation(versions.test.coordinates)
        implementation(project(":file-fixtures"))
    }
}
