plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    api(project(":base-plugins"))
    implementation(buildConstants.stage0.buildConstants.coordinates)
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.base-jvm-lib", "net.rubygrapefruit.plugins.convention.BaseJvmLibraryPlugin")
}