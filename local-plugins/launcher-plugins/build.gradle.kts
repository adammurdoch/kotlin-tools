plugins {
    id("net.rubygrapefruit.gradle-plugin")
}

group = versions.plugins.group

dependencies {
    api(project(":base-plugins"))
    implementation(versions.libs.coordinates("download"))
}

pluginBundle {
    plugin("net.rubygrapefruit.jvm.native-binary", "net.rubygrapefruit.plugins.app.internal.plugins.NativeBinaryJvmLauncherPlugin")
    plugin("net.rubygrapefruit.jvm.ui-app", "net.rubygrapefruit.plugins.app.internal.plugins.JvmUiApplicationPlugin")
    plugin("net.rubygrapefruit.native.ui-app", "net.rubygrapefruit.plugins.app.internal.plugins.NativeUiApplicationPlugin")
}
