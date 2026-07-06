plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    targetJvmVersion = 26
    dependencies {
        implementation(project(":kmp-lib"))
        implementation(project(":kmp-lib-render"))
        implementation(project(":jvm-lib"))
    }
    test {
        implementation(versions.test.coordinates)
    }
}
