plugins {
    id("net.rubygrapefruit.stage2.kmp.lib")
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
