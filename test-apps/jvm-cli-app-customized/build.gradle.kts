plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    appName = "app"
    mainClass = "sample.MainKt"
    module.name = "sample.app"
    targetJavaVersion = 11
}

dependencies {
    implementation(project(":kmp-lib-customized"))
    implementation(project(":jvm-lib-customized"))
}
