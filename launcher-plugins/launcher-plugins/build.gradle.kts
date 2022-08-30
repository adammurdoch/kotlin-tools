plugins {
    id("net.rubygrapefruit.gradle-plugin")
}

group = "net.rubygrapefruit.plugins"

dependencies {
    implementation(project(":download"))
}

gradlePlugin {
    plugins {
        create("native-binary") {
            id = "net.rubygrapefruit.jvm.native-binary"
            implementationClass = "net.rubygrapefruit.app.plugins.NativeBinaryJvmLauncherPlugin"
        }
    }
}
