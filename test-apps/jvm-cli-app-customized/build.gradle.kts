plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    appName = "app"
    mainClass = "sample.MainKt"
    module.name = "sample.app"
    targetJvmVersion = 11

    dependencies {
        implementation(project(":kmp-lib-customized"))
        implementation(project(":kmp-lib-render-customized"))
        implementation(project(":jvm-lib-customized"))
    }
}
