plugins {
    id("net.rubygrapefruit.jvm.ui-app")
}

application {
    mainClass.set("sample.MainKt")
    module.requires.add("kotlin.stdlib")
    module.requires.add("java.desktop")
    module.requires.add("sample.calc")
}

dependencies {
    implementation(project(":mpp-lib"))
    implementation(project(":jvm-lib"))
}
