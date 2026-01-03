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
    plugin("net.rubygrapefruit.convention.base-jvm-lib", "net.rubygrapefruit.plugins.convention.BaseJvmLibraryPlugin")
    plugin("net.rubygrapefruit.convention.build-jvm-lib", "net.rubygrapefruit.plugins.convention.BuildJvmLibraryPlugin")
    plugin("net.rubygrapefruit.convention.jvm-lib", "net.rubygrapefruit.plugins.convention.ConventionJvmLibraryPlugin")
}