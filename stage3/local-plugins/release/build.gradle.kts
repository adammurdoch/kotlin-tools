plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
    id("net.rubygrapefruit.stage2.serialization")
}

dependencies {
    implementation(buildConstants.kotlin.plugin.coordinates)
    implementation(project(":model"))
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.release", "net.rubygrapefruit.plugins.release.internal.ReleasePlugin")
}
