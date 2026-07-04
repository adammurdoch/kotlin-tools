plugins {
    id("net.rubygrapefruit.kmp.lib")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = 11
        module.name = "net.rubygrapefruit.file_fixtures"
        dependencies {
            api(versions.test.junit.coordinates)
        }
    }
    nativeDesktop()
    common {
        api(versions.test.coordinates)
        api(project(":file-io"))
    }
}
