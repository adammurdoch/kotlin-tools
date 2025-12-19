plugins {
    id("net.rubygrapefruit.stage1.gradle-plugin")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(buildConstants.foojay.plugin.coordinates)
}

pluginBundle {
    plugin("net.rubygrapefruit.stage2.settings", "net.rubygrapefruit.plugins.stage2.SettingsPlugin")
    plugin("net.rubygrapefruit.stage2.included-build", "net.rubygrapefruit.plugins.stage2.IncludedBuildPlugin")
}