plugins {
    id("net.rubygrapefruit.plugins.stage1.gradle-plugin")
}

pluginBundle {
    plugin("net.rubygrapefruit.plugins.stage2.serialization", "net.rubygrapefruit.plugins.stage2.SerializationPlugin")
}