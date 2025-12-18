plugins {
    id("net.rubygrapefruit.plugins.stage0.build-constants")
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(buildConstants.kotlin.plugin.coordinates)
    implementation(buildConstants.stage0.buildConstants.coordinates)
}

gradlePlugin {
    plugins {
        create("gradlePluginPlugin") {
            id = "net.rubygrapefruit.plugins.stage1.gradle-plugin"
            implementationClass = "net.rubygrapefruit.plugins.stage1.GradlePluginPlugin"
        }
    }
}
