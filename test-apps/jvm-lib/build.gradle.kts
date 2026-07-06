plugins {
    id("net.rubygrapefruit.jvm.lib")
}

library {
    module.name = "sample.system"
    test {
        implementation(versions.test.coordinates)
    }
}
