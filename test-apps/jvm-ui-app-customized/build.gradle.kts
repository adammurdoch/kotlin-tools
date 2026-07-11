plugins {
    id("net.rubygrapefruit.jvm.ui-app")
}

application {
    appName = "app"
    module.name = "sample.app"
    mainClass = "sample.MainKt"
    module.requires.add("java.desktop")
    targetJvmVersion = 17

    dependencies {
        implementation(project(":kmp-lib-customized"))
        implementation(project(":jvm-lib-customized"))
    }
    test {
        implementation(versions.test.coordinates)
    }
}
