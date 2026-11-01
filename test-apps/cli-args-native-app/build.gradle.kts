plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    nativeDesktop()
    common {
        implementation(versions.libs.coordinates("cli-app"))
    }
}