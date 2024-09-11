plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.render"
        targetJavaVersion = 11
    }
    common {
        implementation(project(":kmp-lib"))
    }
}
