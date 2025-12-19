plugins {
    id("net.rubygrapefruit.plugins.stage2.gradle-plugin")
    id("net.rubygrapefruit.plugins.stage2.serialization")
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
