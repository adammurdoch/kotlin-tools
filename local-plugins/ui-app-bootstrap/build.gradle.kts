plugins {
    id("net.rubygrapefruit.kmp.lib")
}

group = versions.libs.group

library {
    jvm {
        module.name = "net.rubygrapefruit.ui_app_bootstrap"
    }
    macOS()
    common {
        implementation(project(":app-bootstrap"))
    }
}
