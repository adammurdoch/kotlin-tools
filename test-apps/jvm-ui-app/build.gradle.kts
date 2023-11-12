plugins {
    id("net.rubygrapefruit.jvm.ui-app")
}

application {
    mainClass = "sample.MainKt"
    module.requires.add("java.desktop")

    dependencies {
        implementation(project(":kmp-lib"))
        implementation(project(":jvm-lib"))
    }
}
