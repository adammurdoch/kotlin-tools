plugins {
    id("net.rubygrapefruit.plugins.stage2.gradle-plugin")
    id("net.rubygrapefruit.plugins.stage2.serialization")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(project(":model"))
    implementation(buildConstants.serialization.library.json.coordinates)
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.release", "net.rubygrapefruit.plugins.release.internal.ReleasePlugin")
}
