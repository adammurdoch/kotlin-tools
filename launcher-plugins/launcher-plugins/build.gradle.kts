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
        create("jvm-ui-app") {
            id = "net.rubygrapefruit.jvm.ui-app"
            implementationClass = "net.rubygrapefruit.app.plugins.JvmUiApplicationPlugin"
        }
    }
}
