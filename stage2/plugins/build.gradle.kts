plugins {
    id("net.rubygrapefruit.plugins.stage1.gradle-plugin")
}

dependencies {
    implementation(buildConstants.stage1.plugins.coordinates)
}

pluginBundle {
    plugin(buildConstants.stage2.plugins.gradlePlugin.id, "net.rubygrapefruit.plugins.stage2.GradlePluginPlugin")
    plugin("net.rubygrapefruit.plugins.stage2.serialization", "net.rubygrapefruit.plugins.stage2.SerializationPlugin")
}