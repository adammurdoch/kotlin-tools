pluginManagement {
    includeBuild("../stage1")
}
plugins {
    id("net.rubygrapefruit.stage1.settings")
}

include("plugins")
include("settings-plugins")
