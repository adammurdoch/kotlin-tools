plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("net.rubygrapefruit.jvm.native-binary")
}

application {
    mainClass.set("sample.MainKt")

    module.requires.add("jvmLib")
}

dependencies {
    implementation(project(":jvm-lib"))
}