plugins {
    id("net.rubygrapefruit.kmp.lib")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = 11
        module.name = "net.rubygrapefruit.app_launcher"
    }
    nativeDesktop()
}
