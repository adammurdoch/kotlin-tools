plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    mainClass.set("sample.MainKt")
    module.requires.add("sample.parser")
}

dependencies {
    implementation(project(":mpp-lib"))
}
