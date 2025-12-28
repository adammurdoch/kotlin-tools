plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
}

dependencies {
    implementation(project(":machine-info"))
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.test-apps", "net.rubygrapefruit.plugins.internal.TestAppsPlugin")
}
