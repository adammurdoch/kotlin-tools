plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
    id("net.rubygrapefruit.stage2.serialization")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    api(buildConstants.kotlin.plugin.coordinates)
    implementation(project(":settings-plugins"))
    implementation(project(":build-constants"))
    implementation(project(":basics"))
    implementation(project(":bytecode"))
    implementation(project(":machine-info"))
    testImplementation(buildConstants.kotlin.test.coordinates)
}

pluginBundle {
    plugin("net.rubygrapefruit.kotlin-base", "net.rubygrapefruit.plugins.app.internal.plugins.KotlinBasePlugin")
    plugin("net.rubygrapefruit.gradle-plugin", "net.rubygrapefruit.plugins.app.internal.plugins.GradlePluginPlugin")
    plugin("net.rubygrapefruit.kmp.base-lib", "net.rubygrapefruit.plugins.app.internal.plugins.KmpBaseLibraryPlugin")
    plugin("net.rubygrapefruit.kmp.lib", "net.rubygrapefruit.plugins.app.internal.plugins.KmpLibraryPlugin")
    plugin("net.rubygrapefruit.native.desktop-lib", "net.rubygrapefruit.plugins.app.internal.plugins.NativeDesktopLibraryPlugin")
    plugin("net.rubygrapefruit.native.base-cli-app", "net.rubygrapefruit.plugins.app.internal.plugins.NativeCliApplicationBasePlugin")
    plugin("net.rubygrapefruit.native.cli-app", "net.rubygrapefruit.plugins.app.internal.plugins.NativeCliApplicationPlugin")
    plugin("net.rubygrapefruit.jvm.lib", "net.rubygrapefruit.plugins.app.internal.plugins.JvmLibraryPlugin")
    plugin("net.rubygrapefruit.jvm.cli-app", "net.rubygrapefruit.plugins.app.internal.plugins.JvmCliApplicationPlugin")
    plugin("net.rubygrapefruit.jvm.embedded-jvm", "net.rubygrapefruit.plugins.app.internal.plugins.EmbeddedJvmLauncherPlugin")
}
