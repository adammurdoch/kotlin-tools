import net.rubygrapefruit.plugins.stage0.BuildConstants

plugins {
    id("net.rubygrapefruit.plugins.stage0.build-constants")
    id("java-gradle-plugin")
}

dependencies {
    implementation(BuildConstants.stage0.buildConstantsCoordinates)
}

gradlePlugin {
    plugins {
        create("gradlePluginPlugin") {
            id = "net.rubygrapefruit.plugins.stage1.gradle-plugin-plugin"
            implementationClass = "net.rubygrapefruit.plugins.stage1.GradlePluginPlugin"
        }
    }
}
