plugins {
    id("net.rubygrapefruit.jvm.ui-app")
}

application {
    appName = "app"
    module.name = "sample.app"
    mainClass = "sample.MainKt"
    module.requires.add("java.desktop")
}

dependencies {
    implementation(project(":mpp-lib"))
    implementation(project(":jvm-lib"))
}
