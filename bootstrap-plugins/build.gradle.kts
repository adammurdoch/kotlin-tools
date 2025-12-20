plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    api(buildConstants.kotlin.plugin.coordinates)
    implementation(buildConstants.foojay.plugin.coordinates)
    implementation(project(":build-constants"))
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.jvm-base", "net.rubygrapefruit.plugins.bootstrap.JvmBasePlugin")
    plugin("net.rubygrapefruit.bootstrap.jvm.lib", "net.rubygrapefruit.plugins.bootstrap.JvmLibraryPlugin")
    plugin("net.rubygrapefruit.bootstrap.jni.lib", "net.rubygrapefruit.plugins.bootstrap.JniLibraryPlugin")
    plugin("net.rubygrapefruit.bootstrap.kmp.lib", "net.rubygrapefruit.plugins.bootstrap.KmpLibraryPlugin")
    plugin("net.rubygrapefruit.bootstrap.settings", "net.rubygrapefruit.plugins.bootstrap.SettingsPlugin")
}
