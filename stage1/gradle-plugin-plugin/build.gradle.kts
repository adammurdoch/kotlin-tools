plugins {
    id("net.rubygrapefruit.stage0.build-constants")
    id("net.rubygrapefruit.stage0.java-gradle-plugin")
}

group = buildConstants.stage1.plugins.group

dependencies {
    implementation(buildConstants.kotlin.plugin.coordinates)
    implementation(buildConstants.stage0.buildConstants.coordinates)
}

gradlePlugin {
    plugins {
        create("gradlePlugin") {
            id = buildConstants.stage1.plugins.gradlePlugin.id
            implementationClass = "net.rubygrapefruit.plugins.stage1.GradlePluginPlugin"
        }
        create("jvmLibrary") {
            id = buildConstants.stage1.plugins.jvmBase.id
            implementationClass = "net.rubygrapefruit.plugins.stage1.JvmBasePlugin"
        }
    }
}
