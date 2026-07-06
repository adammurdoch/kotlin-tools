plugins {
    id("net.rubygrapefruit.jvm.lib")
}

library {
    module.name = "sample.system"
    targetJvmVersion = 11
    test {
        implementation(versions.test.coordinates)
    }
}
