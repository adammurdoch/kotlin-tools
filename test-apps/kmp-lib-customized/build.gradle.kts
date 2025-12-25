plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.calc"
        targetJvmVersion = 11
    }
}
