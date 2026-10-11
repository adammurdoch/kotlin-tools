plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    nativeDesktop()
    common {
        implementation(versions.libs.coordinates("cli-app"))
        implementation(versions.libs.coordinates("file-io"))
        implementation(versions.libs.coordinates("parse"))
        implementation(versions.libs.coordinates("file-parse"))
    }
}