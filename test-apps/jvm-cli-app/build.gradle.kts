plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    dependencies {
        implementation(project(":kmp-lib"))
        implementation(project(":jvm-lib"))
    }
}
