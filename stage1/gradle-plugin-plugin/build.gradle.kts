import net.rubygrapefruit.plugins.stage0.BuildConstants

plugins {
    id("net.rubygrapefruit.plugins.stage0.build-constants")
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(BuildConstants.kotlin.pluginCoordinates + ":" + BuildConstants.kotlin.version)
}

gradlePlugin {
    plugins {
        create("gradlePluginPlugin") {
            id = "net.rubygrapefruit.plugins.stage1.gradle-plugin-plugin"
            implementationClass = "net.rubygrapefruit.plugins.stage1.GradlePluginPlugin"
        }
    }
}
