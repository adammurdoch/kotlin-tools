plugins {
    id("net.rubygrapefruit.kmp.lib")
}

library {
    jvm {
        module.name = "sample.calc"
    }
    nativeDesktop()
    browser()
    test {
        implementation(versions.test.coordinates)
    }
}
