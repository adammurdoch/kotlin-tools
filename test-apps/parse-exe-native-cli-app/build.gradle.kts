plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    entryPoint = "sample.main"
    nativeDesktop()
    common {
        implementation(versions.libs.coordinates("cli-app"))
        implementation(versions.libs.coordinates("file-io"))
        implementation(versions.libs.coordinates("parse"))
    }
}