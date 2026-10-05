plugins {
    id("net.rubygrapefruit.kmp.lib")
}

group = versions.libs.group

library {
    jvm {
        module.name = "net.rubygrapefruit.ui_app_launcher"
    }
    macOS()
    common {
        implementation(project(":app-launcher"))
    }
}
