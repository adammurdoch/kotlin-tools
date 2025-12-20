plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(buildConstants.foojay.plugin.coordinates)
    implementation(project(":build-constants"))
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.settings", "net.rubygrapefruit.plugins.SettingsPlugin")
}
