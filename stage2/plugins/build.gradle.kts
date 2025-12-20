plugins {
    id("net.rubygrapefruit.stage1.gradle-plugin")
}

dependencies {
    implementation(buildConstants.kotlin.plugin.coordinates)
    implementation(buildConstants.stage1.plugins.gradlePlugin.coordinates)
}

pluginBundle {
    plugin(buildConstants.stage2.plugins.gradlePlugin.id, "net.rubygrapefruit.plugins.stage2.GradlePluginPlugin")
    plugin("net.rubygrapefruit.stage2.jvm.lib", "net.rubygrapefruit.plugins.stage2.JvmLibraryPlugin")
    plugin("net.rubygrapefruit.stage2.serialization", "net.rubygrapefruit.plugins.stage2.SerializationPlugin")
    plugin("net.rubygrapefruit.stage2.jni.lib", "net.rubygrapefruit.plugins.stage2.JniLibraryPlugin")
    plugin("net.rubygrapefruit.stage2.kmp.lib", "net.rubygrapefruit.plugins.stage2.KmpLibraryPlugin")
}