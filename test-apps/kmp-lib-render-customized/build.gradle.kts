plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.render"
        targetJvmVersion = 11
    }
    nativeDesktop()
    desktop {
        dependencies {
            implementation(project(":kmp-lib-customized"))
        }
    }
}
