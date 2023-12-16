import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.gradle-plugin")
}

group = Versions.plugins.group

dependencies {
    implementation("net.rubygrapefruit.libs:download:1.0")
}

gradlePlugin {
    plugins {
        create("native-binary") {
            id = "net.rubygrapefruit.jvm.native-binary"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.NativeBinaryJvmLauncherPlugin"
        }
        create("jvm-ui-app") {
            id = "net.rubygrapefruit.jvm.ui-app"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.JvmUiApplicationPlugin"
        }
        create("native-ui-app") {
            id = "net.rubygrapefruit.native.ui-app"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.NativeUiApplicationPlugin"
        }
    }
}
