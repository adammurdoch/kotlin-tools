plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    appName = "app"
    entryPoint = "sample.main"
    nativeDesktop()
    common {
        implementation(project(":kmp-lib-customized"))
        implementation(project(":kmp-lib-render"))
        implementation(project(":native-lib"))
    }
}
