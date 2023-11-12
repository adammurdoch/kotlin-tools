plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("net.rubygrapefruit.jvm.embedded-jvm")
}

application {
    mainClass = "sample.MainKt"

    dependencies {
        implementation(project(":kmp-lib"))
        implementation(project(":jvm-lib"))
    }
}
