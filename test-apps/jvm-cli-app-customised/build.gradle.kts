plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    appName.set("app")
    mainClass.set("sample.app.MainKt")
    module.name.set("sample.app")
}

dependencies {
    implementation(project(":mpp-lib"))
    implementation(project(":jvm-lib"))
}
