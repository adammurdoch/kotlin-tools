plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    common {
        implementation(project(":kmp-lib"))
        implementation(project(":kmp-lib-render"))
        implementation(project(":native-lib"))
    }
}
