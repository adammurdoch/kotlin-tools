plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    appName = "cli-args-app"
    nativeDesktop()
    common {
        implementation(versions.libs.coordinates("cli-app"))
    }
}