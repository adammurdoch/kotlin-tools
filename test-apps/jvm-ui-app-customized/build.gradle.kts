plugins {
    id("net.rubygrapefruit.jvm.ui-app")
}

application {
    appName = "app"
    module.name = "sample.app"
    mainClass = "sample.MainKt"
    module.requires.add("java.desktop")
    targetJavaVersion = 11
}

dependencies {
    implementation(project(":mpp-lib-customized"))
    implementation(project(":jvm-lib-customized"))
}
