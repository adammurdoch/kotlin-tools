plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    targetJvmVersion = 11
    dependencies {
        implementation(project(":kmp-lib-customized"))
        implementation(project(":kmp-lib-render-customized"))
        implementation(project(":jvm-lib-customized"))
    }
}
