import net.rubygrapefruit.plugins.bootstrap.Versions

plugins {
    id("net.rubygrapefruit.gradle-plugin")
}

group = Versions.pluginsGroup

dependencies {
    implementation("net.rubygrapefruit.libs:download:1.0")
    implementation(Versions.bootstrapPluginCoordinates)
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
        create("native-ui-app") {
            id = "net.rubygrapefruit.native.ui-app"
            implementationClass = "net.rubygrapefruit.app.plugins.NativeUiApplicationPlugin"
        }
    }
}
