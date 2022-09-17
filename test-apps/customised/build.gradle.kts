plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    appName.set("app")
    mainClass.set("sample.app.MainKt")

    module.name.set("sample.app")
    module.requires.add("sample.parser")
    module.requires.add("jvmLib")
}

dependencies {
    implementation(project(":mpp-lib"))
    implementation(project(":jvm-lib"))
}
