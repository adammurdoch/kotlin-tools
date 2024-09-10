plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.render"
    }
    common {
        implementation(project(":kmp-lib"))
    }
}
