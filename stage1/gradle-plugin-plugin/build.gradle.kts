plugins {
    id("net.rubygrapefruit.plugins.stage0.build-constants")
    id("net.rubygrapefruit.plugins.stage0.java-gradle-plugin")
}

group = buildConstants.stage1.plugins.group

dependencies {
    implementation(buildConstants.kotlin.plugin.coordinates)
    implementation(buildConstants.stage0.buildConstants.coordinates)
}

gradlePlugin {
    plugins {
        create("gradlePluginPlugin") {
            id = buildConstants.stage1.plugins.gradlePlugin.id
            implementationClass = "net.rubygrapefruit.plugins.stage1.GradlePluginPlugin"
        }
    }
}
