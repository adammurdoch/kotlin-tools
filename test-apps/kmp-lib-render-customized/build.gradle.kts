plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.render"
        targetJvmVersion = 11
    }
    nativeDesktop()
    browser()
}
