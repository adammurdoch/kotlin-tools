plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.render"
        targetJavaVersion = 11
    }
    desktop {
        dependencies {
            implementation(project(":kmp-lib-customized"))
        }
    }
}
