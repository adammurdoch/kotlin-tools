plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
    id("net.rubygrapefruit.stage2.serialization")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(gradleTestKit())
    implementation(buildConstants.serialization.library.json.coordinates)
    implementation(buildConstants.production.bootstrapPlugins.coordinates)
    implementation(project(":model"))
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.samples", "net.rubygrapefruit.plugins.samples.internal.SamplesPlugin")
}
