plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    targetJvmVersion = 25
    dependencies {
        implementation(project(":kmp-lib"))
        implementation(project(":kmp-lib-render"))
        implementation(project(":jvm-lib"))
    }
}
