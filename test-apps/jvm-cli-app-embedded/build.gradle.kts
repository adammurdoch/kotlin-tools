plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("net.rubygrapefruit.jvm.embedded-jvm")
}

application {
    dependencies {
        implementation(project(":kmp-lib"))
        implementation(project(":jvm-lib"))
    }
}
