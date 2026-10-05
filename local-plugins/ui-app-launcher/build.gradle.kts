plugins {
    id("net.rubygrapefruit.kmp.lib")
}

group = versions.plugins.group

library {
    jvm()
    macOS()
    common {
        implementation(project(":app-launcher"))
    }
}
