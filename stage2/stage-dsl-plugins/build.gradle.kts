plugins {
    id("net.rubygrapefruit.stage1.gradle-plugin")
}

dependencies {
    implementation(buildConstants.kotlin.plugin.coordinates)
    implementation(project(":plugins"))
}

pluginBundle {
    plugin("net.rubygrapefruit.stage2.stage-dsl", "net.rubygrapefruit.plugins.stage2.StageDslPlugin")
}