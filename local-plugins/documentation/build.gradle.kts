plugins {
    id("net.rubygrapefruit.stage2.gradle-plugin")
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.commonmark:commonmark:0.22.0")
    implementation(project(":model"))
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.docs", "net.rubygrapefruit.plugins.docs.internal.DocsPlugin")
}
