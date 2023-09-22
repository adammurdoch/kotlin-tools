plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    appName = "app"
    mainClass = "sample.app.MainKt"
    module.name = "sample.app"
}

dependencies {
    implementation(project(":mpp-lib"))
    implementation(project(":jvm-lib"))
}
