plugins {
    id("net.rubygrapefruit.native.ui-app")
}

application {
    entryPoint = "sample.main"
    test {
        implementation(versions.test.coordinates)
    }
}
