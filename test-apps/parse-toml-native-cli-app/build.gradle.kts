plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    appName = "parse-toml-cli-app"
    nativeDesktop()
    common {
        implementation(versions.libs.coordinates("cli-app"))
        implementation(versions.libs.coordinates("file-io"))
        implementation(versions.libs.coordinates("toml"))
        implementation(versions.libs.coordinates("file-parse"))
        implementation(project(":kmp-lib-render"))
    }
}