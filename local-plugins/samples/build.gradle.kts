plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
    id("net.rubygrapefruit.stage2.serialization")
}

dependencies {
    implementation(gradleTestKit())
    implementation(project(":build-constants"))
    implementation(project(":model"))
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.samples", "net.rubygrapefruit.plugins.samples.internal.SamplesPlugin")
}
