plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.calc"
    }
    common {
        api("net.rubygrapefruit:parse:1.0")
    }
}
