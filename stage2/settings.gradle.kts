pluginManagement {
    includeBuild("../stage1")
}
plugins {
    id("net.rubygrapefruit.plugins.stage1.settings")
}

include("plugins")
include("settings-plugins")
