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
        create("settingsPlugin") {
            id = "net.rubygrapefruit.stage1.settings"
            implementationClass = "net.rubygrapefruit.plugins.stage1.SettingsPlugin"
        }
    }
}
