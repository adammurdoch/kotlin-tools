plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.render"
    }
    desktop {
        dependencies {
            implementation(project(":kmp-lib"))
        }
    }
}
