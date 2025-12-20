plugins {
    id("net.rubygrapefruit.stage0.build-constants")
    id("net.rubygrapefruit.stage0.java-gradle-plugin")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(buildConstants.foojay.plugin.coordinates)
    implementation(buildConstants.stage0.buildConstants.coordinates)
}

gradlePlugin {
    plugins {
        create("settings") {
            id = buildConstants.stage1.plugins.settings.id
            implementationClass = "net.rubygrapefruit.plugins.stage1.SettingsPlugin"
        }
        create("includedBuild") {
            id = buildConstants.stage1.plugins.includedBuild.id
            implementationClass = "net.rubygrapefruit.plugins.stage1.IncludedBuildPlugin"
        }
    }
}
