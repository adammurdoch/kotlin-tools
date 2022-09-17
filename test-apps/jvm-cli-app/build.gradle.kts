plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    mainClass.set("sample.MainKt")

    module.requires.add("sample.parser")
    module.requires.add("jvmLib")
}

dependencies {
    implementation(project(":mpp-lib"))
    implementation(project(":jvm-lib"))
}
