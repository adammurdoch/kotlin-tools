plugins {
    id("net.rubygrapefruit.convention.base-kmp-lib")
}

library {
    jvm {
        targetJvmVersion = 11
        dependencies {
            implementation(project(":cpu-info"))
        }
    }
    nativeDesktop()
}
