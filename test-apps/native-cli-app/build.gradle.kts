plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    common {
        implementation(project(":kmp-lib"))
        implementation(project(":native-lib"))
    }
}
