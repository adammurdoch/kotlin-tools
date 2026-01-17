plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    dependencies {
        implementation(project(":parse-kmp-lib"))
    }
}