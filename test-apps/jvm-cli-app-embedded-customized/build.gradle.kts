plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("net.rubygrapefruit.jvm.embedded-jvm")
}

application {
    appName = "app"
    mainClass = "sample.MainKt"
    module.name = "sample.app"
    targetJavaVersion = 11

    dependencies {
        implementation(project(":kmp-lib-customized"))
        implementation(project(":kmp-lib-render"))
        implementation(project(":jvm-lib-customized"))
    }
}
