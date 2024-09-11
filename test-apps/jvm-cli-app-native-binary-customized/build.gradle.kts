plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("net.rubygrapefruit.jvm.native-binary")
}

application {
    appName = "app"
    mainClass = "sample.MainKt"
    module.name = "sample.app"

    dependencies {
        implementation(project(":kmp-lib"))
        implementation(project(":kmp-lib-render"))
        implementation(project(":jvm-lib"))
    }
}
