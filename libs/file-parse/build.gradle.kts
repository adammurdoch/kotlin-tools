plugins {
    id("net.rubygrapefruit.kmp.lib")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = versions.libs.jvm.version
        module.name = "net.rubygrapefruit.file-parse"
    }
    nativeDesktop()

    common {
        implementation(project(":parse"))
        implementation(project(":file-io"))
    }
    test {
        implementation(versions.test.coordinates)
        implementation(versions.libs.coordinates("file-fixtures"))
    }
}
