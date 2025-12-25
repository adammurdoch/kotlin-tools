plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = versions.plugins.java
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
