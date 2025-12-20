plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
}

dependencies {
    api(buildConstants.kotlin.plugin.coordinates)
    implementation(project(":build-constants"))
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.jni.lib", "net.rubygrapefruit.plugins.bootstrap.JniLibraryPlugin")
    plugin("net.rubygrapefruit.bootstrap.kmp.lib", "net.rubygrapefruit.plugins.bootstrap.KmpLibraryPlugin")
}
