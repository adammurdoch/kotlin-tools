pluginManagement {
    includeBuild("../stage0")
    includeBuild("../stage1")
}
plugins {
    id("net.rubygrapefruit.plugins.stage0.settings")
}

include("plugins")
