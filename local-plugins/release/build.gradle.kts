plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
    id("net.rubygrapefruit.stage2.serialization")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(project(":model"))
    implementation(buildConstants.kotlin.plugin.coordinates)
    implementation(buildConstants.serialization.library.json.coordinates)
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.release", "net.rubygrapefruit.plugins.release.internal.ReleasePlugin")
}
