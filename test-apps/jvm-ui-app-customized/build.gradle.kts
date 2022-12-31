plugins {
    id("net.rubygrapefruit.jvm.ui-app")
}

application {
    appName.set("app")
    module.name.set("sample.app")
    mainClass.set("sample.MainKt")
    module.requires.add("java.desktop")
}

dependencies {
    implementation(project(":mpp-lib"))
    implementation(project(":jvm-lib"))
}
