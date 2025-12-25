plugins {
    id("net.rubygrapefruit.gradle-plugin")
}

group = versions.plugins.group

dependencies {
    api(project(":base-plugins"))
    implementation(versions.libs.coordinates("download"))
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
